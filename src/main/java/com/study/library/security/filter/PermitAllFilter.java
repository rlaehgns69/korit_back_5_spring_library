package com.study.library.security.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@Component
public class PermitAllFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        List<String> antMatchers = List.of("/error", "/server", "/auth");

        String uri = request.getRequestURI();
        request.setAttribute("isPermitAll", false);
        for(String antMatcher : antMatchers) {
            if(uri.startsWith(antMatcher)) {
            //if(uri.contains(antMatcher)) {
                // string안에 들어있는 function
                // /account/principal /error antmatcher 포함하고있니?
                // 두번째 auth, server포함 이것도 포함.
                request.setAttribute("isPermitAll", true);//권한이 필요없는 거
                
                // request 요청 때 생성
            }
        }


//        System.out.println(uri);
        filterChain.doFilter(request, response);
    }
}
