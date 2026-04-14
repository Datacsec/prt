package com.bnpp.irb.datalake.portail.controller;

import com.bnpp.irb.datalake.portail.models.User;
import com.bnpp.irb.datalake.portail.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**

- Endpoints REST pour la gestion des utilisateurs et de la session.
- Appelés par Vue.js.
  */
  @RestController
  @RequestMapping(”/api”)
  public class AuthController {
  
  private final UserRepository userRepository;
  
  public AuthController(UserRepository userRepository) {
  this.userRepository = userRepository;
  }
  
  /**
  - GET /api/me
  - Retourne les infos de l’utilisateur connecté.
  - Appelé par Vue.js au démarrage.
    */
    @GetMapping(”/me”)
    public ResponseEntity<?> getCurrentUser(
    @AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
    
    if (principal == null) {
    return ResponseEntity.status(401).body(Map.of(“error”, “Non authentifié”));
    }
    
    String uid = principal.getName();
    Optional<User> userOpt = userRepository.findByUid(uid);
    
    if (userOpt.isEmpty()) {
    return ResponseEntity.status(403).body(Map.of(“error”, “Utilisateur non autorisé”));
    }
    
    User user = userOpt.get();
    return ResponseEntity.ok(Map.of(
    “uid”,    user.getUid(),
    “nom”,    user.getNom(),
    “prenom”, user.getPrenom(),
    “role”,   user.getRole()
    ));
    }
  
  /**
  - GET /api/auth/status
  - Vérifie si une session est active.
    */
    @GetMapping(”/auth/status”)
    public ResponseEntity<?> getAuthStatus(
    @AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
    return ResponseEntity.ok(Map.of(“authenticated”, principal != null));
    }
  
  /**
  - GET /api/admin/users
  - Liste tous les utilisateurs autorisés.
    */
    @GetMapping(”/admin/users”)
    public ResponseEntity<?> getAllUsers() {
    return ResponseEntity.ok(userRepository.findAll());
    }
  
  /**
  - POST /api/admin/users
  - Ajoute un utilisateur autorisé.
  - Effet immédiat sans redémarrage.
    */
    @PostMapping(”/admin/users”)
    public ResponseEntity<?> addUser(@RequestBody User newUser) {
    if (userRepository.findByUid(newUser.getUid()).isPresent()) {
    return ResponseEntity.badRequest().body(
    Map.of(“error”, “UID ‘” + newUser.getUid() + “’ existe déjà”)
    );
    }
    return ResponseEntity.ok(userRepository.save(newUser));
    }
  
  /**
  - DELETE /api/admin/users/{uid}
  - Supprime un utilisateur.
  - Effet immédiat sans redémarrage.
    */
    @DeleteMapping(”/admin/users/{uid}”)
    public ResponseEntity<?> deleteUser(@PathVariable String uid) {
    Optional<User> userOpt = userRepository.findByUid(uid);
    if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
    userRepository.delete(userOpt.get());
    return ResponseEntity.ok(Map.of(“message”, “Utilisateur ‘” + uid + “’ supprimé”));
    }
  
  /**
  - GET /access-denied
  - Page retournée quand l’UID SAML n’est pas en base.
    */
    @GetMapping(”/access-denied”)
    public ResponseEntity<?> accessDenied() {
    return ResponseEntity.status(403).body(
    Map.of(“error”, “Accès refusé”,
    “message”, “Votre compte n’est pas autorisé. Contactez votre administrateur.”)
    );
    }
    }