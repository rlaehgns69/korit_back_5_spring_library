package com.study.library.jwt;

import com.study.library.entity.User;
import com.study.library.repository.UserMapper;
import com.study.library.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;
    private UserMapper userMapper; //Autowired 안달고 생성자에서
    // 생성자에 무조건 주입 위에다 달아놨으면 이녀석에 대한 생성자만 Usermapper 매개변수가 Usermapper뿐
    // 우리가 필요한건 Secret key를 가지고 Keys.돌아가지고
    public JwtProvider(@Value("${jwt.secret}") String secret, 
                       @Autowired UserMapper userMapper) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.userMapper = userMapper;
    }
    // 요키값으로 풀고 /암호화 할거다 -Jwt 고정
    // 우리가만들어준거기 때문에 자동 생성 x
    // 매개변수로 만들어서 직접넣어주는 방법이 있다.
    public String generateToken(User user) {


//        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        int userId = user.getUserId();
        String username = user.getUsername(); //이 두개의 정보
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities(); // 권한 추가
        Date expiredDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24)); //만료시간
        // new Date 현재날짜의시간(지금시간)을 가지고 와서 하루를 더해라 -하루가 더해진 날짜객체가 만들어진다.
        // new Date(날짜+하루)=미래시간 만료날짜

        // authentication.getName();
        // principalUser.getName(); implements UserDetails안에 들어있는 것들 다 쓸수 있다. getter
        String accessToken = Jwts.builder() // Jwt- JSON Web token
                .claim("userId", userId) // key-value (JSON)
                .claim("username", username) // 커스텀된 키값 밸류값 claim
                .claim("authorities", authorities) // JWT에 권한까지 .
                .setExpiration(expiredDate) //만료시간 정해져 있음.
                .signWith(key, SignatureAlgorithm.HS256)// 암호화() key값과 암호화 알고리즘
                // 직접넣어도 된다. /생성기를 통해 넣을거다.
                .compact();

        return accessToken;// 최종목표
    }

    public String removeBearer(String token) {
        if(!StringUtils.hasText(token)) { // ifnull or isempty isBlack String에서
            //StringUtils.hasText framework null인지 공백이 있는지 등 체크
            return null;//문자열이 아니면 null
        }
        return token.substring("Bearer ".length()); //문자열이 들어왔다 .Bearer제거
        // 01234 문자열도 index 0,5 매개변수로 01234출력(잘라서) Bearer 0123456
        // 0, 0부터끝까지
        //filter에서 사용 토큰을 이용해서 검증
    }

    public Claims getClaims(String token) {
        Claims claims = null;

        try {
            claims = Jwts.parserBuilder()
                    //build전 setSigninKey
                    .setSigningKey(key)
                    .build()
                    // 어떤 키로 열건가
                    .parseClaimsJws(token) // Jws
                    // token을 claim로
                    .getBody();
            //유효하지않으면 Claims = null;
        } catch (Exception e) {
            // 만료 등 유효하지않으면 예외
            log.error("JWT 인증 오류: {}", e.getMessage());
        }

        return claims;
    }
    public Authentication getAuthentication(Claims claims) {
        String username = claims.get("username").toString(); //Object-> String
        User user = userMapper.findUserByUsername(username);
        if(user == null) {
            // 토큰은 유효하지만 DB에서 User정보가 삭제되었을 경우
            return null;
        }
        // claims까지 넘어갔다면 유효한 토큰 쓸 수 있는 토큰 당연히 존재하는 유저
        // claims userName꺼냈는데 db에서 지워버렸음(db에서 x) token은 유효하지만 db에 없는 경우가 있다.
        // db에 당연히 있어야하는 user지만 login했고 token 가지고 있지만 username으로 못찾아옴. db에서  그런경우 null
        PrincipalUser principalUser = user.toPrincipalUser();
        return new UsernamePasswordAuthenticationToken(principalUser, principalUser.getPassword(), principalUser.getAuthorities()); // 비어있는 문자열 비밀번호는 비워있어도 상관없음.
    //상위가 Authentication 업캐스팅 되서 리턴
        // 그러면 AuthenticationFilter
        
        // 권한 설정 3개 3번째 권한이 빈배열이라서 문제가 됨
        // token 없으면 403 권한이 필요해서
        // 로그인 200 정상
        // 권한을 받기위해서 filter를 거칠 것임.
    }
}
