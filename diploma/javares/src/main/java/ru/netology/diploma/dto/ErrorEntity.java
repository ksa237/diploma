package ru.netology.diploma.dto;

/// в этом классе описывается сущность характеризующая ошибку возвращаемую со стороны бэкенд
/// в doc-API ошибка описывается совершенно коккретно, поля имеют разные типы, поэтому Map использовать неудобно
public class ErrorEntity {

    private String message; //собственно текст ошибки
    private Integer id; // видимо код ошибки

    public ErrorEntity(String message, Integer id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
