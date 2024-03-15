package com.study.library.jwt;

import com.study.library.security.PrincipalUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }// 요키값으로 풀고 /암호화 할거다 -Jwt 고정

    public String generateToken(Authentication authentication) {




        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        int userId = principalUser.getUserId();
        String username = principalUser.getUsername(); //이 두개의 정보
        Date expiredDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24)); //만료시간
        // new Date 현재날짜의시간(지금시간)을 가지고 와서 하루를 더해라 -하루가 더해진 날짜객체가 만들어진다.
        // new Date(날짜+하루)=미래시간 만료날짜

        // authentication.getName();
        // principalUser.getName(); implements UserDetails안에 들어있는 것들 다 쓸수 있다. getter
        String accessToken = Jwts.builder() // Jwt- JSON Web token
                .claim("userId", userId) // key-value (JSON)
                .claim("username", username) // 커스텀된 키값 밸류값 claim
                .setExpiration(expiredDate) //만료시간 정해져 있음.
                .signWith(key, SignatureAlgorithm.HS256)//암호화 () key값과 암호화 알고리즘
                // 직접넣어도 된다. /생성기를 통해 넣을거다.
                .compact();

        return accessToken;// 최종목표
    }
}
