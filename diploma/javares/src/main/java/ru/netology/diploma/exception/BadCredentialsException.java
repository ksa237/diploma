package ru.netology.diploma.exception;

public class BadCredentialsException extends AppException{
    //здесь создадим исключения вида - неправильные данные авторизации - имя пользователя, пароль
    public BadCredentialsException(String message) {
        super(message);
    }
}
