package com.study.library.entity;

import com.study.library.security.PrincipalUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private List<RoleRegister> roleRegisters;//1번유저가 1 ROLE_TEMP_USER,2 ROLE,3번(USER,ADMIN)
    // 권한을 가질 수 있다.(JOIN) user->RoleRegister 1(userEntity 1,2,3 ROLE TEMP / USER / ADMIN

    // List를 
    // Collection set list 상위 extends GrantedAuthorities
    public List<SimpleGrantedAuthority> getAuthorities() {
        //stream안쓰고
//        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        for(RoleRegister roleRegister : roleRegisters) {
//            authorities.add(new SimpleGrantedAuthority(roleRegister.getRole().getRoleName()));
//            // roleRegisters안에 roleRegister . getRole() 롤안에 getRoleName();
//            // security는 grantedAuthoriy로 만들어진다.
//        }
//        return authorities;
        return roleRegisters.stream().map(roleRegister ->
                                    new SimpleGrantedAuthority(roleRegister.getRole().getRoleName()))//새로운stream
                .collect(Collectors.toList());//을 리스트로
        // 방법3 roleRegisters안에 메서드 생성해도 된다.
    }

    public PrincipalUser toPrincipalUser() {
        return PrincipalUser.builder()
                .userId(userId)
                .username(username)
                .name(name)
                .email(email)
                .authorities(getAuthorities()) // roleRegisters를 authories로 변환하고 넣는 방식
                // getAuthories를 실행하면 리스트로 바꾸고
                //PrincipalUser  에 있는 get
                .build();
    }
}
