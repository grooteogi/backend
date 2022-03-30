package grooteogi.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static grooteogi.config.JwtExpirationEnum.ACCESS_TOKEN_EXPIRATION_TIME;
import static grooteogi.config.JwtExpirationEnum.REFRESH_TOKEN_EXPIRATION_TIME;

@Configuration
public class JwtProvider {
    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;
    public String generateAccessToken( int id, String email ){
        return doGenerateToken( id, email, ACCESS_TOKEN_EXPIRATION_TIME.getValue() );
    }
    public String generateRefreshToken( int id, String email ){
        return doGenerateToken( id, email, REFRESH_TOKEN_EXPIRATION_TIME.getValue() );
    }
    public String doGenerateToken( int id, String email, long expireTime ){
        Claims claims = Jwts.claims();
        claims.put("ID", id);
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Map verifyToken(String authorizationHeader ){
        validationAuthorizationHeader(authorizationHeader);
        String token = extractToken(authorizationHeader);
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            Claims claims =  Jwts.parser()
                    .setSigningKey(SECRET_KEY) // (3)
                    .parseClaimsJws(token) // (4)
                    .getBody();
            result.put( "result", true );
            result.put( "email", (String) claims.get("email") );
        } catch (ExpiredJwtException e) {
            result.put( "result", false );
            result.put( "msg", e.getMessage() );
        } catch (JwtException e) {
            result.put( "result", false );
            result.put( "msg", e.getMessage() );
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
