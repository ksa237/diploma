package ru.netology.diploma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.diploma.dto.ErrorMessage;

@RestControllerAdvice
public class ExceptionApiHandler {

    //https://struchkov.dev/blog/ru/exception-handling-controlleradvice/#
    @ExceptionHandler(EmptyListOfFilesException.class)
    public ResponseEntity<ErrorMessage> handleException(EmptyListOfFilesException exception) {
        //здесь обработаем исключение если запрашиваемый список файлов по пользователю - пуст

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage(), 1));
    }


}
