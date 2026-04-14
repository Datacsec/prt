package com.bnpparibas.irb.datalake.frservices.config;

import com.bnpparibas.irb.datalake.frservices.entity.User;
import com.bnpparibas.irb.datalake.frservices.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * USER TOOL - Version SQLite (remplace l'ancienne version passFile.txt).
 *
 * AVANT : lisait passFile.txt ligne par ligne avec format uid:pwd:role:bank
 * APRÈS : interroge la base SQLite via UserRepository
 *
 * TOUTES LES SIGNATURES DE MÉTHODES SONT CONSERVÉES pour ne pas casser
 * le code existant (ServerSecurityConfigSAML, ServiceController, etc.)
 *
 * Changement clé : la classe est maintenant un @Component Spring
 * (plus static) car elle a besoin du UserRepository injecté.
 * Les méthodes gardent leurs noms mais ne sont plus static —
 * voir note de migration ci-dessous.
 *
 * ⚠️ NOTE DE MIGRATION :
 * Si votre code existant appelle UserTool.getUserRole(uid) en statique,
 * vous devrez soit :
 *   a) Injecter UserTool là où c'est utilisé (@Autowired UserTool userTool)
 *   b) Ou utiliser le pattern "static holder" (voir bas du fichier)
 */
@Component
public class UserTool {

    private static final Logger logger = LoggerFactory.getLogger(UserTool.class);

    private final UserRepository userRepository;

    // Pattern static holder pour compatibilité avec les appels statiques existants
    private static UserTool instance;

    public UserTool(UserRepository userRepository) {
        this.userRepository = userRepository;
        instance = this; // permet les appels UserTool.getUserRole() statiques
    }

    // ================================================================
    // MÉTHODES PRINCIPALES - Remplacement direct de l'ancien passFile.txt
    // ================================================================

    /**
     * Récupère le rôle d'un utilisateur depuis SQLite.
     *
     * AVANT : getUserPropertiesByIndex(uid, propertiesIndex: 2)
     *   → lisait la 3ème colonne de passFile.txt
     * APRÈS : SELECT role FROM users WHERE uid = ?
     *
     * @param uid identifiant BNP (NameID SAML)
     * @return le rôle (ex: "ADMIN", "USER") ou "" si UID non trouvé
     *         (comportement identique à l'ancien : "" → NOTAUSER → accès refusé)
     */
    public static String getUserRole(String uid) {
        if (instance == null) {
            logger.error("UserTool non initialisé - UserRepository non disponible");
            return "";
        }
        Optional<User> user = instance.userRepository.findByUid(uid);
        String role = user.map(User::getRole).orElse("");
        logger.info("getUserRole({}) → '{}'", uid, role.isEmpty() ? "NOTFOUND" : role);
        return role;
    }

    /**
     * Récupère la banque autorisée d'un utilisateur depuis SQLite.
     *
     * AVANT : getUserPropertiesByIndex(uid, propertiesIndex: 3)
     *   → lisait la 4ème colonne de passFile.txt
     * APRÈS : SELECT bank FROM users WHERE uid = ?
     *
     * @param uid identifiant BNP
     * @return la banque autorisée (ex: "ALL_BANK", "BNPP") ou "" si non trouvé
     */
    public static String getUserBank(String uid) {
        if (instance == null) return "";
        Optional<User> user = instance.userRepository.findByUid(uid);
        String bank = user.map(User::getBank).orElse("");
        logger.info("getUserBank({}) → '{}'", uid, bank);
        return bank;
    }

    // ================================================================
    // MÉTHODES DE CONTEXTE - Récupère l'uid de l'utilisateur connecté
    // ================================================================

    /**
     * Récupère la banque de l'utilisateur actuellement connecté.
     *
     * Logique identique à l'ancienne version :
     *   - Si SAML → extrait l'UID depuis les credentials SAML
     *   - Si Spring auth → utilise getName()
     *
     * CONSERVÉ IDENTIQUE pour ne pas casser le code existant.
     */
    public static String getCurrentUserBank() {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();

        // SAML configuration
        if (loggedInUser.getPrincipal().toString().contains("springframework.security.saml2")) {
            String password = (String) loggedInUser.getCredentials();

            if (password != null && password.contains("caprizia.github.io")) {
                // dev local - extrait l'UID depuis le XML SAML brut
                String capriziaTag = "<saml:Subject><saml:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">";
                int startAttributeValue = password.indexOf(capriziaTag) + capriziaTag.length();
                int endAttributeValue = password.indexOf("</saml:NameID>");
                return getUserBank(password.substring(startAttributeValue, endAttributeValue));
            } else {
                return getUserBank(getAttributeValue(password, "uid"));
            }
        } else {
            // Spring auth classique
            return getUserBank(loggedInUser.getName());
        }
    }

    // ================================================================
    // MÉTHODE getUserDataFromRequest - Conservée identique
    // ================================================================

    /**
     * Extrait les données utilisateur (uid + role) depuis la requête HTTP.
     *
     * AVANT : lisait le rôle dans passFile.txt via getUserRole(uid)
     * APRÈS : même logique mais getUserRole() lit dans SQLite
     *
     * @return Map avec "uid" et "role"
     */
    public static Map<String, String> getUserDataFromRequest(HttpServletRequest request) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();

        // parse value from credentials for saml - decoded password in spring
        String password = (String) loggedInUser.getCredentials();
        // get userName from request for spring auth
        String userName = request.getRemoteUser();

        String uid = "";

        // SAML configuration
        if (userName != null && userName.contains("springframework.security.saml2")) {
            if (password != null && password.contains("caprizia.github.io")) { // dev local
                String capriziaTag = "<saml:Subject><saml:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">";
                int startAttributeValue = password.indexOf(capriziaTag) + capriziaTag.length();
                int endAttributeValue = password.indexOf("</saml:NameID>");
                uid = password.substring(startAttributeValue, endAttributeValue);
            } else {
                uid = getAttributeValue(password, "uid");
            }
        } else {
            uid = userName != null ? userName : "";
        }

        // ✅ CHANGEMENT : getUserRole lit maintenant SQLite au lieu de passFile.txt
        String roleFromDB = getUserRole(uid);

        Map<String, String> userIntel = new HashMap<>();
        userIntel.put("role", roleFromDB);
        userIntel.put("uid", uid);

        return userIntel;
    }

    // ================================================================
    // MÉTHODE checkUserAuthorization - Conservée identique
    // ================================================================

    /**
     * Vérifie si l'utilisateur est autorisé à accéder à une banque donnée.
     *
     * Logique identique à l'ancienne version :
     *   - Si roleFromDB == "" → NOTAUSER → redirect /notAllowed.html
     *   - Si bank non vide → vérifie que l'utilisateur a accès à cette banque
     *
     * CONSERVÉ IDENTIQUE car la logique métier ne change pas.
     *
     * @return uid de l'utilisateur ou "" si non autorisé
     */
    public static String checkUserAuthorization(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 String bank) {
        Map<String, String> userData = getUserDataFromRequest(request);

        // get role from SQLite (was passFile.txt)
        String roleFromDB = userData.get("role");
        String uid = userData.get("uid");

        // disable unknown user (not in SQLite → roleFromDB == "")
        if (roleFromDB.equals("")) {
            response.setHeader("Location", "/notAllowed.html");
            response.setStatus(302);
            logger.info("User '{}' is not authorized (not found in database)", uid);
        } else if (!bank.equals("")) {
            // control authorization to request about a bank
            String authorizedBankFromDB = getUserBank(uid);

            logger.info("authorizedBankFromDB: {}", authorizedBankFromDB);
            logger.info("bank: {}", bank);

            if (!(authorizedBankFromDB.contains("ALL_BANK") || authorizedBankFromDB.contains(bank))) {
                response.setStatus(403);
            }
        }

        return uid;
    }

    // ================================================================
    // MÉTHODE getAttributeValue - Conservée IDENTIQUE (parsing XML SAML)
    // ================================================================

    /**
     * Parse un attribut depuis le XML d'une assertion SAML.
     * Cette méthode reste inchangée - elle parse le XML brut.
     */
    public static String getAttributeValue(String xmlSource, String attributeName) {
        try {
            int startAttribute = xmlSource.indexOf("<ns2:Attribute Name=\"" + attributeName + "\"");
            String attribute = xmlSource.substring(startAttribute);

            int startAttributeValueTag = attribute.indexOf("<ns2:AttributeValue>") + "<ns2:AttributeValue>".length();
            int startValue = attribute.indexOf(">", startAttributeValueTag) + ">".length();
            int endAttributeValueTag = attribute.indexOf("</ns2:AttributeValue>");

            return attribute.substring(startValue, endAttributeValueTag);
        } catch (Exception e) {
            return "";
        }
    }
}
