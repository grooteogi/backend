package grooteogi.utils;

import static grooteogi.enums.JwtExpirationEnum.ACCESS_TOKEN_EXPIRATION_TIME;
import static grooteogi.enums.JwtExpirationEnum.REFRESH_TOKEN_EXPIRATION_TIME;

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

  public String generateAccessToken(int id, String email) {
    return doGenerateToken(id, email, ACCESS_TOKEN_EXPIRATION_TIME.getValue());
  }

  public String generateRefreshToken(int id, String email) {
    return doGenerateToken(id, email, REFRESH_TOKEN_EXPIRATION_TIME.getValue());
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
    try {
      Jwts.parser()
          .setSigningKey(secretKey)
          .parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      throw new ApiException(ApiExceptionEnum.MALFORED_TOKEN_EXCEPTION);
    } catch (ExpiredJwtException e) {
      throw new ApiException(ApiExceptionEnum.EXPIRED_TOKEN_EXCEPTION);
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
    return authorizationHeader.substring("Bearer ".length());
  }
}
