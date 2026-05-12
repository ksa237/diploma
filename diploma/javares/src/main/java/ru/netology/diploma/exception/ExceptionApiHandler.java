package ru.netology.diploma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.diploma.dto.ErrorEntity;

@RestControllerAdvice
public class ExceptionApiHandler {

//    //https://struchkov.dev/blog/ru/exception-handling-controlleradvice/#


    @ExceptionHandler(BaseIOException.class)
    public ResponseEntity<ErrorEntity> handleException(BaseIOException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(new ErrorEntity(exception.getMessage(), 1));
    }


    @ExceptionHandler(ErrorInputDataException.class)
    public ResponseEntity<ErrorEntity> handleException(ErrorInputDataException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(new ErrorEntity(exception.getMessage(), 1));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorEntity> handleException(BadCredentialsException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(new ErrorEntity(exception.getMessage(), 1));
    }


}
