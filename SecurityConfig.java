package com.bnpp.irb.datalake.portail.config;

import com.bnpp.irb.datalake.portail.service.SamlUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**

- Configuration Spring Security SAML pour portail-datalake.
- - Protège toutes les routes
- - Configure le login SAML2 via WebSSO ITG
- - Vérifie l’UID dans SQLite après auth SAML
- - Configure CORS pour Vue.js
    */
    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    public class SecurityConfig {
  
  private final SamlUserDetailsService samlUserDetailsService;
  
  public SecurityConfig(SamlUserDetailsService samlUserDetailsService) {
  this.samlUserDetailsService = samlUserDetailsService;
  }
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  
  ```
   http
       .cors(cors -> cors.configurationSource(corsConfigurationSource()))
       .csrf(csrf -> csrf.disable())
  
       .authorizeHttpRequests(auth -> auth
           .requestMatchers("/api/public/**").permitAll()
           .requestMatchers("/error").permitAll()
           .requestMatchers("/access-denied").permitAll()
           .anyRequest().authenticated()
       )
  
       .saml2Login(saml -> saml
           // bnpp_websso = registrationId dans application.properties
           .loginProcessingUrl("/login/saml2/sso/{registrationId}")
           .successHandler(samlSuccessHandler())
           .failureUrl("/access-denied")
       )
  
       .saml2Logout(logout -> logout
           .logoutUrl("/logout")
       );
  
   return http.build();
  ```
  
  }
  
  @Bean
  public AuthenticationSuccessHandler samlSuccessHandler() {
  return (request, response, authentication) -> {
  
  ```
       Saml2Authentication saml2Auth = (Saml2Authentication) authentication;
       Saml2AuthenticatedPrincipal principal =
           (Saml2AuthenticatedPrincipal) saml2Auth.getPrincipal();
  
       try {
           // Vérification UID dans SQLite
           samlUserDetailsService.loadUserFromSaml(principal);
           // ✅ Autorisé → redirection vers le frontend Vue.js
           response.sendRedirect("http://localhost:5173/dashboard");
           // ⚠️ En prod, adaptez l'URL selon votre déploiement
       } catch (Exception e) {
           // ❌ UID non trouvé en base → accès refusé
           response.sendRedirect("/access-denied");
       }
   };
  ```
  
  }
  
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
  CorsConfiguration config = new CorsConfiguration();
  config.setAllowedOrigins(List.of(
  “http://localhost:5173”,  // Vue.js dev (Vite)
  “http://localhost:8080”
  ));
  config.setAllowedMethods(List.of(“GET”, “POST”, “PUT”, “DELETE”, “OPTIONS”));
  config.setAllowedHeaders(List.of(”*”));
  config.setAllowCredentials(true);
  
  ```
   UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
   source.registerCorsConfiguration("/**", config);
   return source;
  ```
  
  }
  }