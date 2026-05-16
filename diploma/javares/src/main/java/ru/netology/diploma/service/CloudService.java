package ru.netology.diploma.service;

import org.springframework.stereotype.Service;
import ru.netology.diploma.dto.ResponseFileEntity;
import ru.netology.diploma.exception.BadCredentialsException;
import ru.netology.diploma.repository.CloudRepository;

import java.util.List;
import java.util.Map;

@Service
public class CloudService {

    private final CloudRepository cloudRepository;

    public CloudService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public List<ResponseFileEntity>getAllFiles(Long userId, Integer limit) {
        return cloudRepository.getAllFiles(userId, limit);
    }

    public void processAuthorization(Map<String, String> authData) {

        Boolean resultAuth = cloudRepository.isSuccessAuthorization(authData);

        if (!resultAuth) {
            throw new BadCredentialsException("Неправильные имя пользователя или пароль");
        }

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
