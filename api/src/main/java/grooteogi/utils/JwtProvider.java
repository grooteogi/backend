package grooteogi.utils;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Date;

@Configuration
public class JwtProvider {
    public String generateToken( int id, String email ){
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setIssuer("fresh") // (2)
                .setIssuedAt(now) // (3)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis())) // (4)
                .claim("id", id) // (5)
                .claim("email", email)
                .signWith(SignatureAlgorithm.HS256, "secret") // (6)
                .compact();
    }
}
