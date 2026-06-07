package ru.netology.diploma.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netology.diploma.repository.TokenRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    TokenRepository tokenRepositoryMock;

    @InjectMocks
    AuthService authService;

    @Test
    void isValidToken_return_false() {

        Optional<Long> emptyLong = Optional.empty();
        when(tokenRepositoryMock.findToken(anyString())).thenReturn(emptyLong);
        Boolean actualFindResult = authService.isValidToken("test_token");
        assertFalse(actualFindResult);

    }
}