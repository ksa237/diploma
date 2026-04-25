package ru.netology.diploma.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import ru.netology.diploma.service.CloudService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class CloudController {

    private final CloudService cloudService;

    public CloudController(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorizationMethod(@RequestBody Map<String, String> authData) throws IOException {

        Boolean authSuccess = cloudService.isSuccessAuthorization(authData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<?> response = null;

        if (authSuccess) {
            Map<String, String> body = Map.of("auth-token", "my-token-manafaka");
            response = new ResponseEntity<>(body, headers, HttpStatus.OK);

        } else {
            Map<String, ?> body = Map.of("message", "Error input data", "id", 1);
            response = new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutMethod(@RequestHeader("auth-token") String authToken) {
        // kill auth-token
        return ResponseEntity.ok().build(); // 200
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFileToServer(@RequestHeader("auth-token") String authToken, @RequestParam String filename, HttpServletRequest request) {

        Enumeration<String> headerNames = request.getHeaderNames();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<?> response = null;

        Boolean existContentType = false;
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerName.equals("Content-Type")) {
                existContentType = true;
                if (!headerValue.contains("multipart/form-data")) {
                    //'#/components/schemas/Error'
                    Map<String, ?> body = Map.of("message", "Invalid content type", "id", 1);
                    response = new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
                    return response;
                }
            }
        }

        if (!existContentType) {
            //'#/components/schemas/Error'
            Map<String, ?> body = Map.of("message", "No header Content-Type", "id", 1);
            response = new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
            return response;
        }

        try {
            StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);

            Boolean existContent = false;
            byte[] fileBytes = null;
            MultipartFile file = multipartRequest.getFile("file");
            if (file != null && !file.isEmpty()) {
                fileBytes = file.getBytes();
                existContent = true;
            }

            if (existContent) {
                cloudService.save(1L, filename, fileBytes);
            } else {
                //'#/components/schemas/Error'
                Map<String, ?> body = Map.of("message", "File data is empty", "id", 1);
                response = new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
                return response;
            }

            String hash = multipartRequest.getParameter("hash");

        } catch (Exception e) {
            //'#/components/schemas/Error'
            Map<String, ?> body = Map.of("message", "Error: " + e.getMessage(), "id", 1);
            response = new ResponseEntity<>(body, headers, HttpStatus.UNAUTHORIZED); //401
            return response;
        }


//        -=от Postman=-
//        headers:
//        auth-token: my-token-manafaka
//        User-Agent: PostmanRuntime/7.53.0
//        Accept: */*
//        Postman-Token: 6624744b-5c67-4a7d-835c-43d360416355
//        Host: localhost:8080
//        Accept-Encoding: gzip, deflate, br
//        Connection: keep-alive
//        Cookie: JSESSIONID=B4A9C35701161C28F3ECE3E151B4530F
//        Content-Length: 0

//        от FRONT
//        Headers:Host: localhost:8080
//        Connection: keep-alive
//        Content-Length: 11762
//        sec-ch-ua-platform: "Linux"
//        User-Agent: Mozilla/5.0 (X11; Linux x86_64; Chromium GOST) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36
//        Accept: application/json, text/plain, */*
//        auth-token: Bearer my-token-manafaka
//        Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryEJMpvfZOO5jEUghs
//        sec-ch-ua: "Not(A:Brand";v="8", "Chromium";v="144"
//        sec-ch-ua-mobile: ?0
//        Origin: http://localhost:8081
//        Sec-Fetch-Site: same-site
//        Sec-Fetch-Mode: cors
//        Sec-Fetch-Dest: empty
//        Referer: http://localhost:8081/
//        Accept-Encoding: gzip, deflate, br, zstd
//        Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7


        return ResponseEntity.ok().build(); // 200
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

        Integer delResult = cloudService.delete(1L, filename);

        ResponseEntity<?> response = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (delResult == 200) {
            return ResponseEntity.ok().build();

        } else if (delResult == 400) {
            //'#/components/schemas/Error'
            Map<String, ?> body = Map.of("message", "Error input data", "id", 1);
            response = new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);

        } else if (delResult == 401) {
            //'#/components/schemas/Error'
            Map<String, ?> body = Map.of("message", "Unauthorized error", "id", 1);
            response = new ResponseEntity<>(body, headers, HttpStatus.UNAUTHORIZED);


        } else if (delResult == 500) {
            //'#/components/schemas/Error'
            Map<String, ?> body = Map.of("message", "Error delete file", "id", 1);
            response = new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam Integer limit) {

        //200
        //filename:
        //  type: string
        //  description: File name
        //  required: true
        //
        //size:
        //  type: integer
        //  description: File size in bytes
        //  required: true

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<?> response = null;
        List<Map<String,Object>> bodyList = cloudService.getAllFiles(1L, limit);
        response = new ResponseEntity<>(bodyList, headers, HttpStatus.OK);

        return response;
                //ResponseEntity.ok().build(); // 200
    }

}
