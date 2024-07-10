package com.nerd2.catcare.common;


import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    public String secretKey;

    public String refreshSecretKey;

    public final ThreadLocal<Boolean> threadLocalBoolean = new ThreadLocal<>();

    public void setThreadLocalBoolean(boolean newValue) {
        threadLocalBoolean.set(newValue);
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Value("${jwt.secret}")
    public void setSecretKey(String val) {
        secretKey = val;
    }

    @Value("${jwt.secret.refresh}")
    public void setRefreshSecretKeyKey(String val) {
        refreshSecretKey = val;
    }


    public String createToken(String userID, long expTime, String tokenLevel) {
        String secretKey = "";


       if(expTime == 0){
           throw new RuntimeException("유효하지 않는 토큰 입니다.");
       }
       if(tokenLevel.equals("access")){

           secretKey = this.secretKey;

       } else if (tokenLevel.equals("reToken")){

           secretKey = this.refreshSecretKey;
       } else {
           throw new RuntimeException("Specify token Levle");
       }


        Claims claims = Jwts.claims().setSubject(userID);
        claims.put("userId", userID);
//        claims.put("userName", userName);
//        claims.put("userLevel", userLevel);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userID)
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                .compact();

    }


    // jwt token에서 claims 추출
    public Claims getClaims(String token, String secretKey) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            Claims claim = Jwts.claims().setSubject("");
            return claim;
        }

        return claims;
    }


    public String getSubject(String token, String secret) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token (유효하지않는 토큰) = {}", e);
            return "Invalid";
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token (만료 된 토큰) = {}", e);
            return "Expired";
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token (지원하지 않는 토큰) = {}", e);
            return "Unsupported";
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty (토큰 claims 문자열 비어있음) = {}", e);
            return "Empty";
        } catch (JwtException e) {
            log.info("Key error (키 오류) = {}", e);
            return "Error";
        }
    }


    // 토큰의 유효성 + 만료일자 확인, 추후 로그 설정
    public int validateToken(String key, String jwtToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            return 1;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token (유효하지않는 토큰) = {}", e);
            return 2;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token (만료 된 토큰) = {}", e);
            return 3;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token (지원하지 않는 토큰) = {}", e);
            return 4;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty (토큰 claims 문자열 비어있음) = {}", e);
            return 5;
        } catch (JwtException e) {
            log.info("Key error (키 오류) = {}", e);
            return 6;
        }
    }

}
