package grooteogi.config;

import grooteogi.dto.user.SessionDto;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthentication implements Authentication {

  private SessionDto sessionDto;
  private boolean isAuthenticated;

  public JwtAuthentication(SessionDto sessionDto) {
    this.sessionDto = sessionDto;
    this.isAuthenticated = true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return sessionDto;
  }

  @Override
  public boolean isAuthenticated() {
    return isAuthenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

  }

  @Override
  public String getName() {
    return sessionDto.getEmail();
  }
}