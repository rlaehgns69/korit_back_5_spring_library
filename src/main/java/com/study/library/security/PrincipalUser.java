package com.study.library.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Data // getter가 username도 있다 Data없으면
// userDetails 로그인 객체
public class PrincipalUser implements UserDetails {
    private int userId;
    private String username;
    private String name;
    private String email;
    private List<SimpleGrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //권한
        return authorities;
    }

    @Override
    public String getPassword() {
        return ""; // 빈값
    }

    // 계정 사용기간 만료
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 사용기간 만료
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 비활성화
    @Override
    public boolean isEnabled() {
        return true;
    }
}
// 만료랑 비활성화 다름.
// 이정보들 db에서 갖고옴. 굳이 안함.

// 이후 이메일 인증할 예정 ROLES갖고
// 위에 일단 false풀 거임 하나라도 false이면 로그인안됨.
// 다음 User로