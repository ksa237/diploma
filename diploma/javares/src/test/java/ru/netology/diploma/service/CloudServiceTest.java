package ru.netology.diploma.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netology.diploma.exception.ErrorInputDataException;
import ru.netology.diploma.repository.CloudRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CloudServiceTest {

    @Mock
    CloudRepository cloudRepositoryMock;

    @InjectMocks
    CloudService cloudService;

    @Test
    void actionDelete_return_ErrorInputDataException() {

        doThrow(new ErrorInputDataException("error input data")).when(cloudRepositoryMock).actionDelete(anyLong(), anyString());
        Throwable exception400 = assertThrows(ErrorInputDataException.class, () -> {
            cloudService.actionDelete(1L,"test_filename");
        });
        assertEquals("error input data", exception400.getMessage());

    }
}