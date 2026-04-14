package com.bnpparibas.irb.datalake.frservices.entity;

import jakarta.persistence.*;

/**
 * ENTITÉ USER - Remplace le fichier passFile.txt par une vraie BDD SQLite.
 *
 * Dans l'ancien système, les données étaient stockées dans passFile.txt
 * avec le format : uid:encryptedPassword:role:bank:...
 * (parsé par UserTool.getUserPropertiesByIndex)
 *
 * Maintenant, chaque ligne devient une entrée dans cette table.
 *
 * Colonnes équivalentes à l'ancien format passFile.txt :
 *   - uid      → parts[0] : identifiant BNP (ex: gb12345)
 *   - nom      → ex: Bernard (sn dans SAML/AuthElement)
 *   - prenom   → ex: Marc   (givenName dans SAML/AuthElement)
 *   - role     → parts[2] : rôle applicatif (ex: ADMIN, USER)
 *   - bank     → parts[3] : banque autorisée (ex: ALL_BANK, BNPP)
 *                           (champ utilisé dans checkUserAuthorization)
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifiant unique BNP - correspond à parts[0] dans l'ancien passFile.txt
     * C'est le NameID renvoyé par l'assertion SAML WebSSO.
     */
    @Column(unique = true, nullable = false)
    private String uid;

    /** Nom de famille (sn dans SAML / AuthElement.sn) */
    @Column(nullable = false)
    private String nom;

    /** Prénom (givenName dans SAML / AuthElement.givenName) */
    @Column(nullable = false)
    private String prenom;

    /**
     * Rôle applicatif - correspond à parts[2] dans l'ancien passFile.txt
     * Ex: "ADMIN", "USER", "READONLY"
     * Utilisé par UserTool.getUserRole(uid) → maintenant via UserRepository
     */
    @Column(nullable = false)
    private String role;

    /**
     * Banque autorisée - correspond à parts[3] dans l'ancien passFile.txt
     * Ex: "ALL_BANK", "BNPP", "BNPP,SG"
     * Utilisé par UserTool.getUserBank(uid) pour checkUserAuthorization
     * Si vide ou non utilisé dans votre app, mettez "ALL_BANK" par défaut.
     */
    @Column(nullable = false)
    private String bank;

    // -------------------------------------------------------
    // Constructeurs
    // -------------------------------------------------------

    public User() {}

    public User(String uid, String nom, String prenom, String role, String bank) {
        this.uid = uid;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.bank = bank;
    }

    // -------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------

    public Long getId() { return id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }
}
