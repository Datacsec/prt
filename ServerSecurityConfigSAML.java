package com.bnpparibas.irb.datalake.frservices.config;

import com.bnpparibas.irb.datalake.frservices.repository.UserRepository;
import org.opensaml.saml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;

/**
 * CONFIGURATION SECURITY SAML - Adaptée pour SQLite.
 *
 * CHANGEMENTS par rapport à l'original :
 *   1. authoritiesExtractor lit maintenant SQLite via UserTool.getUserRole()
 *      (UserTool lui-même a été modifié pour lire SQLite au lieu de passFile.txt)
 *   2. Si l'UID n'est pas en BDD → role = "" → NOTAUSER (comportement conservé)
 *   3. Tout le reste est IDENTIQUE à l'original
 *
 * CONSERVÉ IDENTIQUE :
 *   - @Profile("saml")
 *   - WebSecurityConfigurerAdapter (ancienne API, gardée pour compatibilité)
 *   - saml2Login() avec addObjectPostProcessor
 *   - csrf().disable()
 *   - authoritiesMapper
 */
@Profile("saml")
@Configuration
public class ServerSecurityConfigSAML extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerSecurityConfigSAML.class);

    private final UserRepository userRepository;

    /**
     * Injection du UserRepository pour vérification SQLite.
     * UserTool (static holder) sera aussi initialisé automatiquement
     * par Spring via son @Component.
     */
    public ServerSecurityConfigSAML(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * EXTRACTEUR D'AUTORITÉS SAML
     *
     * Appelé après validation de l'assertion SAML.
     * Extrait l'UID du NameID et cherche son rôle dans SQLite.
     *
     * AVANT : UserTool.getUserRole() lisait passFile.txt
     * APRÈS : UserTool.getUserRole() lit la table users dans SQLite
     *
     * Si l'UID n'est PAS dans SQLite :
     *   → roleFromDB = "" → retourne NOTAUSER
     *   → checkUserAuthorization() renverra 302 vers /notAllowed.html
     */
    OpenSaml4AuthenticationProvider.ResponseAuthenticationConverter authoritiesExtractor = (a -> {

        // Extraire le NameID de l'assertion SAML (= UID BNP)
        String user = a.getAssertion().getSubject().getNameID().getValue();
        logger.info("try to connect user: {}", user);

        // ✅ getUserRole() lit maintenant SQLite (plus passFile.txt)
        String roleFromDB = UserTool.getUserRole(user);

        // Comportement identique à l'original :
        // Si UID non trouvé en BDD → role vide → NOTAUSER
        if (roleFromDB.equals("")) {
            logger.info("User '{}' not found in database → NOTAUSER", user);
            return singletonList(new SimpleGrantedAuthority("NOTAUSER"));
        }

        // Construire les GrantedAuthority depuis le rôle SQLite
        // Le rôle peut être multiple séparé par "," (ex: "ADMIN,USER")
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (String roleToAdd : roleFromDB.split(",")) {
            // En SAML, on retire le préfixe "ROLE_" (comportement original conservé)
            grantedAuthorities.add(
                new SimpleGrantedAuthority(roleToAdd.replace("ROLE_", ""))
            );
        }

        return grantedAuthorities;
    });

    // GrantedAuthoritiesMapper - identique à l'original
    OpenSaml4AuthenticationProvider.GrantedAuthoritiesMapper authoritiesMapper = (a -> a);

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // disable CSRF to be able to upload files (identique à l'original)
        http.csrf().disable()

            .authorizeRequests(requests -> requests.anyRequest().authenticated())

            .saml2Login(Saml2LoginConfigurer -> Saml2LoginConfigurer
                .addObjectPostProcessor(new ObjectPostProcessor<OpenSaml4AuthenticationProvider>() {
                    public <O extends OpenSaml4AuthenticationProvider> O postProcess(O samlAuthProvider) {
                        // ✅ Branche notre extracteur SQLite dans le provider SAML
                        samlAuthProvider.setAuthoritiesExtractor(authoritiesExtractor);
                        samlAuthProvider.setAuthoritiesMapper(authoritiesMapper);
                        return samlAuthProvider;
                    }
                })
            );
    }
}
