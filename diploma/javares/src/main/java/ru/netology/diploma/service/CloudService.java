package ru.netology.diploma.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.netology.diploma.dto.ResponseFileEntity;
import ru.netology.diploma.repository.CloudRepository;

import java.util.List;

@Service
public class CloudService {

    private final CloudRepository cloudRepository;

    /// тестирование контекста приложения
    private final ApplicationContext context;

    public CloudService(CloudRepository cloudRepository
            , ApplicationContext context /// тестирование контекста приложения
    ) {
        this.cloudRepository = cloudRepository;
        this.context = context; /// тестирование контекста приложения
    }

    public List<ResponseFileEntity> getAllFiles(Long userId, Integer limit) {
        return cloudRepository.getAllFiles(userId, limit);
    }

    public void save(Long userId, String filename, byte[] fileBytes) {
        cloudRepository.save(userId, filename, fileBytes);
    }

    public void actionDelete(Long userId, String filename) {
        cloudRepository.actionDelete(userId, filename);

    }

    public byte[] get(long userId, String filename) {
        return cloudRepository.getFile(userId, filename);
    }

    public void editFileName(long userId, String filename, String newfilename) {

        cloudRepository.editFileName(userId, filename, newfilename);

    }
}
