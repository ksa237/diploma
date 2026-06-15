package ru.netology.diploma.model;

public class ResponseFile {

    private String filename;
    private Integer size;
    private Long userId;

    public ResponseFile() {
    }


    public ResponseFile(String filename, Integer size, Long userId) {
        this.filename = filename;
        this.size = size;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
