package ru.netology.diploma.service;

import org.springframework.stereotype.Service;
import ru.netology.diploma.dto.ResponseFileEntity;
import ru.netology.diploma.model.ResponseFile;
import ru.netology.diploma.repository.CloudRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CloudService {

    private final CloudRepository cloudRepository;

    public CloudService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public List<ResponseFileEntity> getAllFiles(Long userId, Integer limit) {

        List<ResponseFile> answerList = cloudRepository.getAllFiles(userId, limit); //объект из репозитория, содержит максимум полей БД
        List<ResponseFileEntity> answerListDTO = new ArrayList<>(); //список сущностей DTO, набор полей необходимых только для клиента

        Iterator<ResponseFile> iterator = answerList.iterator();
        while (iterator.hasNext()) {
            ResponseFile elementDB = iterator.next();
            ResponseFileEntity elementDTO = new ResponseFileEntity(elementDB.getFilename(), elementDB.getSize());
            answerListDTO.add(elementDTO);
        }

        return answerListDTO;

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
