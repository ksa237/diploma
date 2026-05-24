package ru.netology.diploma.exception;

public class ErrorInputDataException extends AppException{

    // здесь созданим исключение для ситуаций когда наддые с клиента переданы в ненадлежащем виде

    public ErrorInputDataException(String message) {
        super(message);
    }
}
