package com.study.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity // Security기존적용 -> 세팅 Security따라가라.
@Configuration //ioc에등록 Config컴포넌트-Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // bean 이름
        return new BCryptPasswordEncoder();// new 하면서 ioc에 등록
    } // autowired를 통해서 DI

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http); 원래 세팅 그대로 WebSecurityConfigureAdapter
        http.csrf().disable(); //기본설정 csrf 토큰방식(위조 방지)-security에서 csrf는 검사하지마.
        http.authorizeRequests() // 요청 때 인증
                .antMatchers("/server/**", "/auth/**") // 서버라고하는 요청주소에 뒤에 뭐가오든지간에 인증안해도되는 요청이다.
                .permitAll()//나머지 요청들은 인증받아라. 거꾸로도 가능
                .anyRequest()
                .authenticated();// 요요청들은 인증받지마라.

        // .http객체에 쌓는 중.
    }
}
