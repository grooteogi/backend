package grooteogi.utils;

import static grooteogi.enums.JwtExpirationEnum.ACCESS_TOKEN_EXPIRATION_TIME;
import static grooteogi.enums.JwtExpirationEnum.REFRESH_TOKEN_EXPIRATION_TIME;

import grooteogi.exception.ApiExceptionEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

  public Map verifyToken(String authorizationHeader) {
    validationAuthorizationHeader(authorizationHeader);
    String token = extractToken(authorizationHeader);
    Map<String, Object> result = new HashMap<String, Object>();

    try {
      Claims claims = Jwts.parser().setSigningKey(secretKey) // (3)
          .parseClaimsJws(token) // (4)
          .getBody();
      result.put("result", true);
      result.put("email", (String) claims.get("email"));
      result.put("ID", (Integer) claims.get("ID"));
    } catch (ExpiredJwtException e) {
      result.put("result", false);
      result.put("status", ApiExceptionEnum.EXPIRED_TOKEN_EXCEPTION);
    } catch (JwtException e) {
      result.put("result", false);
      result.put("status", ApiExceptionEnum.UNAUTHORIZED_EXCEPTION);
    }

    return result;
  }

  public Map refreshToken(String authorizationHeader, String refreshToken) {
    // 토큰이 만료되었는지 확인
    Map result = verifyToken(authorizationHeader);
    if ((boolean) result.get("result")) {
      result.put("result", false);
      result.put("status", ApiExceptionEnum.NO_EXPIRED_TOKEN_EXCEPTION);
    } else if (result.get("status") == ApiExceptionEnum.EXPIRED_TOKEN_EXCEPTION) {
      try {
        Claims claims = Jwts.parser().setSigningKey(secretKey) // (3)
            .parseClaimsJws(refreshToken) // (4)
            .getBody();
        result.put("result", true);
        result.put("token",
            doGenerateToken((Integer) claims.get("ID"), (String) claims.get("email"),
                ACCESS_TOKEN_EXPIRATION_TIME.getValue()));
        result.put("email", (String) claims.get("email"));
        result.put("ID", (Integer) claims.get("ID"));
      } catch (ExpiredJwtException e) {
        result.put("result", false);
        result.put("status", ApiExceptionEnum.EXPIRED_REFRESH_TOKEN_EXCEPTION);
      } catch (JwtException e) {
        result.put("result", false);
        result.put("status", ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
      }
    }

    return result;
  }

  private void validationAuthorizationHeader(String header) {
    if (header == null || !header.startsWith("Bearer ")) {
      throw new IllegalArgumentException();
    }
  }

  private String extractToken(String authorizationHeader) {
    return authorizationHeader.substring("Bearer ".length());
  }
}
