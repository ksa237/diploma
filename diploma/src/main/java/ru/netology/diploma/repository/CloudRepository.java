package ru.netology.diploma.repository;


import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CloudRepository {

    public List<String> getAllFiles(Long userId) {

        List<String> examleList = List.of("monday", "tuesday", "wednesday");
        return examleList;
    }

}
