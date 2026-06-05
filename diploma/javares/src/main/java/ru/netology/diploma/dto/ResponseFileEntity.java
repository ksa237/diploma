package ru.netology.diploma.dto;

public class ResponseFileEntity {

    //сущность описывающая файл пользоваетеля. используется для создания списка файлов пользователя который передается на клиент
    //каждый раз когда браузер выполняет ендпойнт /list
    //сущность не содержит данные файла а содержит описание файла, поля filename и size


    //type: object
    //properties:
    //    filename:
    //        type: string
    //        description: File name
    //         required: true
    //    size:
    //        type: integer
    //        description: File size in bytes
    //        required: true

    private String filename;
    private Integer size;

    public ResponseFileEntity(String filename, Integer size) {
        this.filename = filename;
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
