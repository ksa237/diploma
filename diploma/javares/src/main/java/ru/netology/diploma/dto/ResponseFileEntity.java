package ru.netology.diploma.dto;

/// сущность описывающая файл пользоваетеля. используется для создания списка файлов пользователя который передается на клиент
/// каждый раз когда браузер выполняет ендпойнт /list
/// сущность не содержит данные файла а содержит описание файла, поля filename и size
public class ResponseFileEntity {

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
