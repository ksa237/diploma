package ru.netology.diploma.exception;

public class UnauthorizedException extends AppException{
    //здесь описываем ошибку-исключение характеризующую ошибку авторизации, в нашем приложении используется при обнаружении несушествующего токена
    //также может использоваться для случаев просроченного токена, характеризует ошибку сервера
    //UNAUTHORIZED(401, HttpStatus.Series.CLIENT_ERROR, "Unauthorized"),

    public UnauthorizedException(String message) {
        super(message);
    }
}
