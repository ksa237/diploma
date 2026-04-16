package ru.netology.diploma.service;

import org.springframework.stereotype.Service;
import ru.netology.diploma.repository.CloudRepository;

import java.util.List;

@Service
public class CloudService {

    private final CloudRepository cloudRepository;

    public CloudService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }
    public List<String> getAllFiles(Long userId){
        return cloudRepository.getAllFiles(userId);
    }

}
