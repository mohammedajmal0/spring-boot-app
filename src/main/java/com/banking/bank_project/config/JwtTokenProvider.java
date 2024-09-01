package com.banking.bank_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import java.security.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.expiration}")
    private long jwtExpiration;



    public String generateToken(Authentication authentication){
        String username=authentication.getName();
        Date currentDate=new Date();
        Date expiryDate=new Date(currentDate.getTime()+jwtExpiration);
        return Jwts.builder()
        .claim("subject",username)
        .signWith(key())
        .issuedAt(currentDate)
        .expiration(expiryDate)
        .compact();
    }
    private Key key(){
        byte[] bytes=Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUsername(String token){
        SecretKey sKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Claims claims = Jwts.parser()
                            .verifyWith(sKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
        return claims.getSubject();        
    }

    public boolean validateToken(String token){
        SecretKey sKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        try {
            Jwts.parser()
                .verifyWith(sKey)
                .build()
                .parse(token);
            return true;
        }
        catch(ExpiredJwtException | IllegalArgumentException| MalformedJwtException e){
            throw new RuntimeException(e);
        } 
    }
}
