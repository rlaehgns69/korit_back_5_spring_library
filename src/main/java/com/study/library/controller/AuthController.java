package com.study.library.controller;

import com.study.library.aop.annotation.ParamsPrintAspect;
import com.study.library.aop.annotation.ValidAspect;
import com.study.library.dto.SignupReqDto;
import com.study.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/auth") // 공통주소
public class AuthController {

    @Autowired
    private AuthService authService;

    @ValidAspect
//    @ParamsPrintAspect
    @PostMapping("/signup") // dto 유효한지 확인하고 결과를 bindingResult
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult) { //BeanPropertyBindingResult(업)
        if(authService.isDuplcatedByUsername(signupReqDto.getUsername())) {
            ObjectError objectError = new FieldError("username", "username", "이미 존재하는 사용자이름입니다.");
            bindingResult.addError(objectError);
        }


        // 정규식 이후 중복확인
//        if(authService.isDuplcatedByUsername(signupReqDto.getUsername())) {
//            Map<String, String> errorMap = Map.of("user.name", "이미 존재하는 사용자이름입니다.");
//            return ResponseEntity.badRequest().body(errorMap);
//        }
        // 위에서 badRequest뜨거나 서비스에서 에러안뜨면 throw new Exception 정상동작
        authService.signup(signupReqDto);
        return ResponseEntity.created(null).body(true);
        // 회원가입
    }

}
