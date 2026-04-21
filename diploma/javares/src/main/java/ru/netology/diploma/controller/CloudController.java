package ru.netology.diploma.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.diploma.service.CloudService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class CloudController {

    private final CloudService cloudService;

    public CloudController(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    @GetMapping("/list")
    public List<String> getAllFiles(Long userId) {
        return cloudService.getAllFiles(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,?>> authorizationMethod(@RequestBody Map<String, String> authData) throws IOException {

        Boolean authSuccess = cloudService.isSuccessAuthorization(authData);

        Map<String, String> bodyOK = Map.of("auth-token", "my-token-manafaka");
        Map<String, ?> bodyBadRequest = Map.of("message", "Bad credentials", "id", 1);

        ResponseEntity response = null;

        if (authSuccess) {
            response = new ResponseEntity<>(bodyOK, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(bodyBadRequest, HttpStatus.BAD_REQUEST);
        }
        return response;

    }

}
