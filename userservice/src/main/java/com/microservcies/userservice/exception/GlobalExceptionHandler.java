package com.microservcies.userservice.exception;

import com.microservcies.userservice.dto.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ApplicationException> resoureceNotFoundException(ResourceNotFoundException exception){
        ApplicationException exp = ApplicationException.builder()
                .details(exception.getLocalizedMessage())
                .message(exception.getMessage().toString())
                .status(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(exp, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationException> handleAllException(Exception exception){

        ApplicationException exp = ApplicationException.builder()
                .details(exception.getStackTrace().toString())
                .message(exception.getMessage().toString())
                .build();

        return new ResponseEntity<>(exp, HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
