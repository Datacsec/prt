package com.bnpp.irb.datalake.portail.service;

import com.bnpp.irb.datalake.portail.models.User;
import com.bnpp.irb.datalake.portail.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**

- Vérifie après auth SAML que l’UID existe dans SQLite.
- Si non → accès refusé.
  */
  @Service
  public class SamlUserDetailsService {
  
  private final UserRepository userRepository;
  
  public SamlUserDetailsService(UserRepository userRepository) {
  this.userRepository = userRepository;
  }
  
  public CustomUserDetails loadUserFromSaml(Saml2AuthenticatedPrincipal principal) {
  
  ```
   // Extraire l'UID depuis le NameID de l'assertion SAML
   String uid = principal.getName();
   System.out.println("[SAML] Tentative de connexion pour UID: " + uid);
  
   // Chercher dans SQLite
   Optional<User> userOpt = userRepository.findByUid(uid);
  
   // UID non trouvé → refus
   if (userOpt.isEmpty()) {
       System.out.println("[SAML] ACCÈS REFUSÉ - UID non trouvé: " + uid);
       throw new UsernameNotFoundException(
           "Accès refusé : l'utilisateur '" + uid + "' n'est pas autorisé."
       );
   }
  
   User user = userOpt.get();
   System.out.println("[SAML] Connexion autorisée: " + user.getPrenom() + " " + user.getNom());
  
   // ✅ CORRECTION : List<GrantedAuthority> (pas List<SimpleGrantedAuthority>)
   List<GrantedAuthority> authorities = new ArrayList<>();
   authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
  
   return new CustomUserDetails(user, authorities);
  ```
  
  }
  }