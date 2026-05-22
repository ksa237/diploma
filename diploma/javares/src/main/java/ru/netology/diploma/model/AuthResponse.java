package ru.netology.diploma.model;

public class AuthResponse {

    private Long userId;
    private Boolean succAuth;


    public AuthResponse() {
    }

//    public AuthResponse(Long userId, Boolean succAuth) {
//        this.userId = userId;
//        this.succAuth = succAuth;
//    }

    public Boolean getSuccAuth() {
        return succAuth;
    }

    public void setSuccAuth(Boolean succAuth) {
        this.succAuth = succAuth;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
