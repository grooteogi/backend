package grooteogi.utils;

import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtProvider {

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @Value("${spring.jwt.access_token_expiration_time}")
  private Long accessTokenExpirationTime;

  @Value("${spring.jwt.refresh_token_expiration_time}")
  private Long refreshTokenExpirationTime;

  public String generateAccessToken(int id, String email) {
    return doGenerateToken(id, email, accessTokenExpirationTime);
  }

  public String generateRefreshToken(int id, String email) {
    return doGenerateToken(id, email, refreshTokenExpirationTime);
  }

  public String doGenerateToken(int id, String email, long expireTime) {
    Claims claims = Jwts.claims();
    claims.put("ID", id);
    claims.put("email", email);

    return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expireTime))
        .signWith(SignatureAlgorithm.HS256, secretKey).compact();
  }

  public boolean isUsable(String token) {
    if (token.equals("")) {
      return false;
    }

    try {
      Jwts.parser()
          .setSigningKey(secretKey)
          .parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      throw new ApiException(ApiExceptionEnum.MALFORED_TOKEN_EXCEPTION);
    } catch (ExpiredJwtException e) {
      return false;
    }
  }

  public Session extractAllClaims(String token) throws ExpiredJwtException {
    Claims claims = Jwts.parser().setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();

    Session session = new Session();
    session.setEmail((String) claims.get("email"));
    session.setId((Integer) claims.get("ID"));

    return session;
  }

  public String extractToken(String authorizationHeader) {
    return authorizationHeader.equals("")
        ? authorizationHeader : authorizationHeader.substring("Bearer ".length());
  }
}
