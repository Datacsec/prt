package com.bnpparibas.irb.datalake.frservices.element;

/**
 * AUTH ELEMENT - DTO enrichi avec les données SQLite.
 *
 * IDENTIQUE à l'original sauf que :
 *   - roleFromDB est maintenant rempli depuis SQLite (pas passFile.txt)
 *   - Ajout optionnel de nom/prenom depuis SQLite
 *
 * Utilisé pour transporter toutes les infos d'un utilisateur authentifié.
 */
public class AuthElement {

    private String name;
    private String uid;
    private String cn;
    private String sn;          // nom de famille (SAML attribute)
    private String givenName;   // prénom (SAML attribute)
    private String mail;
    private String roleFromDB;  // ← vient maintenant de SQLite (plus de passFile.txt)
    private String roleSAML;
    // Nouveau champ optionnel depuis SQLite
    private String bankFromDB;  // ← vient de SQLite (colonne "bank")

    public AuthElement(String name, String uid, String cn, String sn,
                       String givenName, String mail, String roleFromDB, String roleSAML) {
        this.name = name;
        this.uid = uid;
        this.cn = cn;
        this.sn = sn;
        this.givenName = givenName;
        this.mail = mail;
        this.roleFromDB = roleFromDB;
        this.roleSAML = roleSAML;
    }

    // Getters / Setters - identiques à l'original
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getCn() { return cn; }
    public void setCn(String cn) { this.cn = cn; }

    public String getSn() { return sn; }
    public void setSn(String sn) { this.sn = sn; }

    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getRoleFromDB() { return roleFromDB; }
    public void setRoleFromDB(String roleFromDB) { this.roleFromDB = roleFromDB; }

    public String getRoleSAML() { return roleSAML; }
    public void setRoleSAML(String roleSAML) { this.roleSAML = roleSAML; }

    public String getBankFromDB() { return bankFromDB; }
    public void setBankFromDB(String bankFromDB) { this.bankFromDB = bankFromDB; }
}
