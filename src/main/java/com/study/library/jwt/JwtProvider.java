package com.study.library.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    public String generateToken(Authentication authentication) {

        String accessToken = Jwts.builder()
                .compact();

        return accessToken;// 최종목표
    }
}
