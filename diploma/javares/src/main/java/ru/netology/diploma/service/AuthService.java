package ru.netology.diploma.service;

import org.springframework.stereotype.Service;
import ru.netology.diploma.exception.BadCredentialsException;
import ru.netology.diploma.model.AuthResponse;
import ru.netology.diploma.repository.TokenRepository;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

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
        String userToken = generateToken();

        Long userId = authResponse.getUserId();
        tokenRepository.saveToken(userId, userToken);

        return userToken;

    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isValidToken(String userToken) {
        return tokenRepository.findToken(userToken).isPresent();
    }

    public void invalidateToken(String userToken) {
        tokenRepository.deleteToken(userToken);
    }

}
