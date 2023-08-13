package com.simple.post.controller.advice;


import com.simple.post.enums.ErrorMessages;
import com.simple.post.exception.NotFoundPostException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class PostExceptionHandler {

    @ExceptionHandler(NotFoundPostException.class)
    public ModelAndView notFoundPost() {
        ModelAndView modelAndView = new ModelAndView("thymeleaf/error-page");
        modelAndView.addObject("errorMessage", ErrorMessages.NOT_FOUND_POST_EXCEPTION.getKorean());

        return modelAndView;
    }
}
