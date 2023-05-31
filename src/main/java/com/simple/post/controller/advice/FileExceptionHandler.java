package com.simple.post.controller.advice;

import com.simple.post.enums.ErrorMessages;
import com.simple.post.exception.NotFoundFileException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(NotFoundFileException.class)
    public ModelAndView notFoundFile() {
        ModelAndView modelAndView = new ModelAndView("thymeleaf/error-page");
        modelAndView.addObject("errorMessage", ErrorMessages.NOT_FOUND_FILE_EXCEPTION.getKorean());

        return modelAndView;
    }
}
