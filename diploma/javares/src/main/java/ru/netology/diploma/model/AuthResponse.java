package ru.netology.diploma.model;

public class AuthResponse {

    //создана специальная сущность в которой запоминается инфо о результатах авторизации пользователя
    //успешной или неуспешной
    //при успешной авторизации, класс используется для получения userId дальше для сохранения токена пользователя выданного на сеанс работы

    private Long userId;
    private Boolean succAuth;

    public AuthResponse() {
    }

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
