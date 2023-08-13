package com.simple.post.controller.advice;


import com.simple.post.enums.ErrorMessages;
import com.simple.post.exception.NotMatchPasswordException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class PasswordExceptionHandler {

    @ExceptionHandler(NotMatchPasswordException.class)
    public ModelAndView notMatchPassword() {
        ModelAndView modelAndView = new ModelAndView("thymeleaf/error-page");
        modelAndView.addObject("errorMessage", ErrorMessages.NOT_MATCH_PASSWORD_EXCEPTION.getKorean());

        return modelAndView;
    }
}
