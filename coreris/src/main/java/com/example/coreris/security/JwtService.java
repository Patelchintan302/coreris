package com.example.coreris.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    //plane text secrete key to secure jave secret key
    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    //generate the token
    public String generateToken(UserDetails user){
        String userRole = user.getAuthorities().iterator().next().getAuthority();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role",userRole)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    //extract the username form the token
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    //checks whether the token is valid
    public boolean isTokenValid(String token, UserDetails user){
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    //checks whether the token is expired or not
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    //extract the expiration from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //take token and extract all is claims ,runs resolvers functions to get exact date field ypu want.
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        return claimsResolver.apply(extractAllClaims(token));
    }

    //uses the given secrete key to verify whether the signature is valid and decrypt the token plyload and return all the claims (key value pairs)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



}
