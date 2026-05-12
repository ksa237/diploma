package ru.netology.diploma.dto;

public class ErrorEntity {

    //Error:
    //type: object
    //    properties:
    //        message:
    //            type: string
    //            description: Error message
    //        id:
    //            type: integer

    private String message;
    private Integer id;

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
