package com.microservcies.hotel.hotelservice.exception;


import com.microservcies.hotel.hotelservice.dto.ApplicationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApplicationError> handleResourceNotFoundException(ResourceNotFoundException e){
        ApplicationError error = new ApplicationError();
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.OK.toString());
        error.setDetails(e.getStackTrace().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationError> handleAllExceptionType(Exception e){
        ApplicationError error = new ApplicationError();
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        error.setDetails(e.getStackTrace().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


}
