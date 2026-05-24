package ru.netology.diploma.exception;

public class BaseDataAccessException extends AppException{

    //здесь создадим исключение которое возникает при работе с БД postgres

    public BaseDataAccessException(String message) {
        super(message);
    }
}
