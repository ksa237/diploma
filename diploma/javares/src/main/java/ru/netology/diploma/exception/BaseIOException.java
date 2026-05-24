package ru.netology.diploma.exception;

public class BaseIOException extends AppException{

    //базовую (типовую) ошибку исключение тоже оборачиваем нашей оберткой, так как
    //когда появиться это исключение а у нас это в file.getBytes(), то нам нужно вернуть не просто информацию об ошибке, а
    //сущность ErrorEntity, у которой помимо сообщения еще есть поле ИД

    public BaseIOException(String message) {
        super(message);
    }
}
