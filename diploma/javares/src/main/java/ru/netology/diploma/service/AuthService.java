package ru.netology.diploma.service;

import org.springframework.stereotype.Service;
import ru.netology.diploma.exception.BadCredentialsException;
import ru.netology.diploma.model.AuthResponse;
import ru.netology.diploma.repository.TokenRepository;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final String TOKEN_PREFIX = "Bearer";

    private final TokenRepository tokenRepository;

    public AuthService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String processAuthorization(Map<String, String> authData) {


        AuthResponse authResponse = tokenRepository.isSuccessAuthorization(authData);

        if (!authResponse.getSuccAuth()) {
            throw new BadCredentialsException("Неправильные имя пользователя или пароль");
        }

        //если обошлись без исключения BadCredentialsException, значит генерируем токен
        String userToken = generateToken(); // для authData

        Long userId = authResponse.getUserId();
        tokenRepository.saveToken(userId, userToken);

        return userToken;

    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isValidToken(String userToken) {
        String parseToken = userToken.replaceFirst("^"+TOKEN_PREFIX+"\\s*", "");
        return tokenRepository.findToken(parseToken).isPresent();
    }

    public void invalidateToken(String userToken) {
        String parseToken = userToken.replaceFirst("^"+TOKEN_PREFIX+"\\s*", "");
        tokenRepository.deleteToken(parseToken);
    }

    public Long getUserIdByToken(String userToken) {
        String parseToken = userToken.replaceFirst("^"+TOKEN_PREFIX+"\\s*", "");
        return tokenRepository.getUserIdByToken(parseToken);

    }
}
