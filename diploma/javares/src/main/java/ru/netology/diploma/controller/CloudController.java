package ru.netology.diploma.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Map<String, String>> authorizationMethod(HttpServletRequest req) throws IOException {

        Map<String, String> response = Map.of("auth-token", "my-token-manafaka");

        return ResponseEntity.ok(response);

    }

}
