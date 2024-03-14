package com.study.library.service;

import com.study.library.dto.SignupReqDto;
import com.study.library.entity.User;
import com.study.library.exception.SaveException;
import com.study.library.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean isDuplcatedByUsername(String username) {
        return userMapper.findUserByUsername(username) != null;
    }

    @Transactional(rollbackFor = Exception.class) //저장프로시저 -- 두개의 동작 Transactional
    public void signup(SignupReqDto signupReqDto) {
        int successCount = 0;
        User user = signupReqDto.toEntity(passwordEncoder);

        successCount += userMapper.saveUser(user); // 쿼리1
        // 1
        successCount += userMapper.saveRole(user.getUserId()); // 쿼리2
        // 1
        if(successCount < 2) {
           //공통으로 badRequeset exception
            throw new SaveException(); // runTimeException->Exception -> rollbackfor
        } // 회원가입완료
        // successCount 무조건 2이상
        
        // 쿼리 한개 exception rollback sql x
        // 동작끝나고 commit
    }
}




//    public void signup(SignupReqDto signupReqDto) {
//        // DTO -> Entity
//        // property key
//        User user = signupReqDto.toEntity();
//        userMapper.saveUser(user);// 여러번 부르기 가능  user.getUserId();
//        // 저장시 password 1234이렇게 보이면 안 된다. password암호화
//        // user를 가지고 세이브를


