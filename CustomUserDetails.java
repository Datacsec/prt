package com.bnpp.irb.datalake.portail.service;

import com.bnpp.irb.datalake.portail.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**

- Encapsule l’entité User avec les infos Spring Security.
- Accessible dans les controllers via @AuthenticationPrincipal.
  */
  public class CustomUserDetails implements UserDetails {
  
  private final User user;
  private final List<GrantedAuthority> authorities;
  
  public CustomUserDetails(User user, List<GrantedAuthority> authorities) {
  this.user = user;
  this.authorities = authorities;
  }
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
  
  @Override
  public String getPassword() { return null; } // pas de password avec SAML
  
  @Override
  public String getUsername() { return user.getUid(); }
  
  @Override
  public boolean isAccountNonExpired() { return true; }
  
  @Override
  public boolean isAccountNonLocked() { return true; }
  
  @Override
  public boolean isCredentialsNonExpired() { return true; }
  
  @Override
  public boolean isEnabled() { return true; }
  
  // Accesseurs vers l’entité User
  public User getUser() { return user; }
  public String getUid() { return user.getUid(); }
  public String getNom() { return user.getNom(); }
  public String getPrenom() { return user.getPrenom(); }
  public String getRole() { return user.getRole(); }
  }