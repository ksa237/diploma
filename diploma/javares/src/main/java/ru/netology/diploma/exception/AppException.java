package ru.netology.diploma.exception;

// обертка над старнадртым исключением RuntimeException
// идея взята из источника //https://struchkov.dev/blog/ru/exception-handling-controlleradvice/#
// по сути этот классом мы будем создавать классифицированные ошибки-исключения для своего серверного ответа
//вместо неинформативного RuntimeException

public class AppException extends RuntimeException{

    public AppException(String message) {
        super(message);
    }

}
