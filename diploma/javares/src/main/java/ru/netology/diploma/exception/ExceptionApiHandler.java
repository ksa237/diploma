package ru.netology.diploma.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.diploma.dto.ErrorEntity;

@RestControllerAdvice
public class ExceptionApiHandler {

    //https://struchkov.dev/blog/ru/exception-handling-controlleradvice/#


    @ExceptionHandler(BaseIOException.class)
    public ResponseEntity<ErrorEntity> baseIOException(BaseIOException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(new ErrorEntity(exception.getMessage(), 1));
    }


    @ExceptionHandler(ErrorInputDataException.class)
    public ResponseEntity<ErrorEntity> errorInputDataException(ErrorInputDataException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(new ErrorEntity(exception.getMessage(), 1));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorEntity> badCredentialsException(BadCredentialsException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) //400
                .body(new ErrorEntity(exception.getMessage(), 1));
    }



    @ExceptionHandler(BaseDataAccessException.class)
    public ResponseEntity<ErrorEntity> dataAccessException(BaseDataAccessException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorEntity(exception.getMessage(),1));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorEntity> unauthorizedException(UnauthorizedException exception){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorEntity(exception.getMessage(), 1));
    }


}
