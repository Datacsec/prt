-- =============================================================
-- SCRIPT D'INITIALISATION SQLite pour frservices
-- Remplace le contenu de passFile.txt
--
-- ANCIEN FORMAT passFile.txt :
--   uid:encryptedPassword:role:bank:extra
--   gb12345:$2a$10$xxxx:ADMIN:ALL_BANK:
--
-- NOUVEAU FORMAT SQLite (table users) :
--   Chaque ligne devient une entrée dans la table
--
-- Exécuter : sqlite3 users.db < init_users.sql
-- =============================================================

CREATE TABLE IF NOT EXISTS users (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    uid     TEXT NOT NULL UNIQUE,
    nom     TEXT NOT NULL,
    prenom  TEXT NOT NULL,
    role    TEXT NOT NULL,
    bank    TEXT NOT NULL DEFAULT 'ALL_BANK'
);

-- =============================================================
-- MIGRATION DEPUIS passFile.txt
-- Pour chaque ligne de l'ancien fichier :
--   uid:encPwd:role:bank → INSERT INTO users (uid, role, bank)
-- Le mot de passe n'est plus nécessaire (auth déléguée à SAML)
-- nom/prenom viennent de l'assertion SAML ou à renseigner manuellement
-- =============================================================

-- Exemples (remplacez avec les vrais UIDs de votre passFile.txt)
INSERT OR IGNORE INTO users (uid, nom, prenom, role, bank)
VALUES ('gb00001', 'Admin', 'Super', 'ADMIN', 'ALL_BANK');

INSERT OR IGNORE INTO users (uid, nom, prenom, role, bank)
VALUES ('gb12345', 'Dupont', 'Jean', 'USER', 'BNPP');

INSERT OR IGNORE INTO users (uid, nom, prenom, role, bank)
VALUES ('gb67890', 'Martin', 'Sophie', 'USER', 'ALL_BANK');

INSERT OR IGNORE INTO users (uid, nom, prenom, role, bank)
VALUES ('gb11111', 'Bernard', 'Marc', 'READONLY', 'BNPP,SG');

-- Vérification
SELECT * FROM users;
