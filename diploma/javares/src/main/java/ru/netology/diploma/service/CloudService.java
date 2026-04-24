package ru.netology.diploma.service;

import org.springframework.stereotype.Service;
import ru.netology.diploma.repository.CloudRepository;

import java.util.List;
import java.util.Map;

@Service
public class CloudService {

    private final CloudRepository cloudRepository;

    public CloudService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public List<String> getAllFiles(Long userId) {
        return cloudRepository.getAllFiles(userId);
    }

    public Boolean isSuccessAuthorization(Map<String, String> authData) {

        return cloudRepository.isSuccessAuthorization(authData);

    }

    public void save(Long userId, String filename, byte[] fileBytes) {
        cloudRepository.save(userId, filename, fileBytes);
    }

    public Integer delete(Long userId, String filename) {
        return cloudRepository.delete(userId, filename);

    }
}
