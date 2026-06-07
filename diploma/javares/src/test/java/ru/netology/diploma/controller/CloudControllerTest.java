package ru.netology.diploma.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netology.diploma.exception.BadCredentialsException;
import ru.netology.diploma.service.AuthService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CloudControllerTest {

    @Mock
    AuthService authServiceMock;

    @InjectMocks
    CloudController cloudController;

    @Test
    void authorizationMethod_return_exeption_bad_credentials() {

        Map<String, String> authDataBad = new HashMap<>();
        authDataBad.put("test_user", "test_password");

        doThrow(new BadCredentialsException("Неправильные имя пользователя или пароль")).when(authServiceMock).processAuthorization(authDataBad);

        Throwable exception =
                assertThrows(BadCredentialsException.class, () -> {
                    cloudController.authorizationMethod(authDataBad);
                });

        assertEquals("Неправильные имя пользователя или пароль", exception.getMessage());

    }

}