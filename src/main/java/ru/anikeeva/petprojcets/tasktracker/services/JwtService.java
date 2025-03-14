package ru.anikeeva.petprojcets.tasktracker.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.models.impl.UserDetailsImpl;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${token.key}")
    private String jwtSigningKey;
    @Value("${token.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(final String token) {
        isTokenAbsent(token);
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(final UserDetails userDetails) {
        isUserDetailsAbsent(userDetails);
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserDetailsImpl customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("role", customUserDetails.getAuthorities().stream().findFirst()
                    .map(GrantedAuthority::getAuthority).orElse("ROLE_USER"));
        }
        return generateToken(claims, userDetails);
    }

    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        isTokenAbsent(token);
        isUserDetailsAbsent(userDetails);
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(final String token, final Function<Claims, T> claimsResolvers) {
        isTokenAbsent(token);
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
        isUserDetailsAbsent(userDetails);
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private boolean isTokenExpired(final String token) {
        isTokenAbsent(token);
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token) {
        isTokenAbsent(token);
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(final String token) {
        isTokenAbsent(token);
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            //ToDo: кастомное исключение
            throw new RuntimeException("Срок действия токена истек", ex);
        } catch (MalformedJwtException ex) {
            //ToDo: кастомное исключение
            throw new RuntimeException("Недопустимый токен", ex);
        } catch (Exception ex) {
            //ToDo: кастомное исключение
            throw new RuntimeException("Не удалось выполнить парсинг токена", ex);
        }
    }

    private void isTokenAbsent(final String token) {
        if (token == null) {
            throw new IllegalArgumentException("Токен отсутствует");
        }
    }

    private void isUserDetailsAbsent(final UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails отсутствует");
        }
    }
}