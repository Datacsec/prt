package com.bnpparibas.irb.datalake.frservices.repository;

import com.bnpparibas.irb.datalake.frservices.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORY USER - Accès SQLite pour remplacer la lecture de passFile.txt.
 *
 * Remplace toutes les lectures de passFile.txt dans UserTool :
 *   - getUserRole(uid)  → findByUid(uid).map(User::getRole)
 *   - getUserBank(uid)  → findByUid(uid).map(User::getBank)
 *
 * Spring Data JPA génère automatiquement le SQL.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Équivalent de getUserPropertiesByIndex(uid, index) dans l'ancien UserTool.
     * Retourne Optional.empty() si l'UID n'est pas dans la BDD
     * → dans ce cas, l'accès sera refusé (comportement identique à l'ancien
     *   roleFromDB.equals("") qui retournait NOTAUSER).
     */
    Optional<User> findByUid(String uid);
}
