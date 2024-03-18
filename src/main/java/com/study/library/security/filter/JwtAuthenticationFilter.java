package com.study.library.security.filter;

import com.study.library.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends GenericFilter {

    @Autowired
    private JwtProvider jwtProvider; // filter Component provider Component

    @Override // filter- servlet filter 동일 doFilter안에 FilterChain
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Boolean isPermitAll = (Boolean) request.getAttribute("isPermitAll");

        if(!isPermitAll) {


            //System.out.println("JWT 필터");-필터까지 들어왔다.
            String accessToken = request.getHeader("Authorization");//instance.js 로컬스토리지 꺼내서 8080으로 무조건
            String removedBearerToken = jwtProvider.removeBearer(accessToken);//jwtProvider에서 호출
            //System.out.println(accessToken);// 토큰 유효 인증 / 유효 하지않으면 인증x
            // claims null
            Claims claims = jwtProvider.getClaims(removedBearerToken);
            // jwtProvider claims 풀어헤침.
            if (claims == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value()); //401
                return;
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증실패
                // 401에러 (401) (httpStatus.UNAUTHORIZED) 저번 로그인 token
            }
            Authentication authentication = jwtProvider.getAuthentication(claims);

            if(authentication == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value()); // 인증실패
                return;
            }
            //System.out.println(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication); // null이 아니라면 인증된 것 null 403
        }
        // permitAll이 아닐 때만 실행-인증이 필요한 상황

        // 전처리
        filterChain.doFilter(request, response);
        // 후처리 AOP랑 동일한 원리
        // AOP proceedingpoint 기준
    }

}
