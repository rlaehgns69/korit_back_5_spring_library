package com.study.library.aop;

import com.study.library.dto.SignupReqDto;
import com.study.library.exception.ValidException;
import com.study.library.repository.UserMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class ValidAop {
    @Autowired
    private UserMapper userMapper;

    @Pointcut("@annotation(com.study.library.aop.annotation.ValidAspect)")
    private void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();

        Object[] args = proceedingJoinPoint.getArgs();
        //args[1]
        BeanPropertyBindingResult bindingResult = null;

        for(Object arg : args) {
            if(arg.getClass() == BeanPropertyBindingResult.class){
                bindingResult = (BeanPropertyBindingResult) arg;
            }
        }

        if(methodName.equals("signup")) {
            SignupReqDto signupReqDto = null;

            for(Object arg : args) {
                if(arg.getClass() == SignupReqDto.class){
                    signupReqDto = (SignupReqDto) arg;
                }
            }
            if(userMapper.findUserByUsername(signupReqDto.getUsername()) != null) {
                ObjectError objectError = new FieldError("username", "username", "이미 존재하는 사용자이다");
                bindingResult.addError(objectError); //signup일 때만
            }
        }

        if(bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            // 반복을 통해서 Map에 에러 담음.
            Map<String, String> errorMap = new HashMap<>();

            for(FieldError fieldError : fieldErrors) {
                String fieldName = fieldError.getField(); // DTO변수명
                String message = fieldError.getDefaultMessage(); // 메세지내용
                System.out.println(fieldName);
                System.out.println(message);
                errorMap.put(fieldName, message); //변수명 키값
            }
            //return ResponseEntity.badRequest().body(errorMap); // controller가 아님. 어라운드에대한 리턴
            // 강제로 예외 터트려서 contollerAdvide로 보냄
            throw new ValidException(errorMap);
        }

        return proceedingJoinPoint.proceed();
    }
}
