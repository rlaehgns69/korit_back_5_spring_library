package com.study.library.aop;

import com.study.library.exception.ValidException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class ValidAop {

    @Pointcut("@annotation(com.study.library.aop.annotation.ValidAspect)")
    private void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        //args[1]
        BeanPropertyBindingResult bindingResult = null;

        for(Object arg : args) {
            if(arg.getClass() == BeanPropertyBindingResult.class){
                bindingResult = (BeanPropertyBindingResult) arg;
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
