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
import tools.jackson.databind.cfg.MapperBuilder;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/")
public class CloudController {

    private final CloudService cloudService;

    private final MapperBuilder mapperBuilder;

    public CloudController(CloudService cloudService, MapperBuilder mapperBuilder) {
        this.cloudService = cloudService;
        this.mapperBuilder = mapperBuilder;
    }

    @GetMapping("/list")
    public List<String> getAllFiles(Long userId, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        int clientPort = request.getRemotePort();
        Logger.getLogger("getAllFiles").log(Level.INFO, "\n" + "clientIp :" + clientIp + "\n" + "clientPort :" + clientPort);
        return cloudService.getAllFiles(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, ?>> authorizationMethod(@RequestBody Map<String, String> authData) throws IOException {

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

    @PostMapping("/file")
    public ResponseEntity<Map<String, ?>> uploadFileToServer(@RequestHeader("auth-token") String authToken, @RequestParam String filename, HttpServletRequest request) {

        //StringBuilder headers = new StringBuilder();
        //headers.append("Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();

        Boolean existContentType = false;
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerName.equals("Content-Type")) {
                existContentType = true;
                if (!headerValue.contains("multipart/form-data")) {
                    Map<String, String> body = Map.of("description", "Invalid content type");
                    ResponseEntity response = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
                    return response;
                }
            }
            //headers.append(String.format("%s: %s\n", headerName, headerValue));
        }
        if (!existContentType) {
            Map<String, String> body = Map.of("description", "No header Content-Type");
            ResponseEntity response = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            return response;
        }
        //headers.append("\n");
        //Logger.getLogger("FilePostMappingLogger").log(Level.INFO, "\n" + headers.toString());

        try {
            StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);

            Boolean existContent = false;
            byte[] fileBytes = null;
            MultipartFile file = multipartRequest.getFile("file");
            if (file != null && !file.isEmpty()) {
                //String fileName = file.getOriginalFilename();
                fileBytes = file.getBytes();
                existContent = true;
                Logger.getLogger("FilePostMappingLogger").log(Level.INFO, "\n" + "Загружен файл: " + filename);
            }

            if (existContent) {
                cloudService.save(1L, filename, fileBytes);
            } else {
                Map<String, String> body = Map.of("description", "File data is empty.");
                ResponseEntity response = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
                return response;
            }

            String hash = multipartRequest.getParameter("hash");
            Logger.getLogger("FilePostMappingLogger").log(Level.INFO, "\n" + "Получен hash: " + hash);


        } catch (Exception e) {
            Map<String, String> body = Map.of("description", "Error: " + e.getMessage());
            ResponseEntity response = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
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
//        java-app-1  | Connection: keep-alive
//        java-app-1  | Content-Length: 11762
//        java-app-1  | sec-ch-ua-platform: "Linux"
//        java-app-1  | User-Agent: Mozilla/5.0 (X11; Linux x86_64; Chromium GOST) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36
//        java-app-1  | Accept: application/json, text/plain, */*
//        java-app-1  | auth-token: Bearer my-token-manafaka
//        java-app-1  | Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryEJMpvfZOO5jEUghs
//        java-app-1  | sec-ch-ua: "Not(A:Brand";v="8", "Chromium";v="144"
//        java-app-1  | sec-ch-ua-mobile: ?0
//        java-app-1  | Origin: http://localhost:8081
//        java-app-1  | Sec-Fetch-Site: same-site
//        java-app-1  | Sec-Fetch-Mode: cors
//        java-app-1  | Sec-Fetch-Dest: empty
//        java-app-1  | Referer: http://localhost:8081/
//        java-app-1  | Accept-Encoding: gzip, deflate, br, zstd
//        java-app-1  | Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7



        Map<String, String> body = Map.of("description", "The file is uploaded to the server");
        ResponseEntity response = new ResponseEntity<>(body, HttpStatus.OK);
        return response;
    }

    @DeleteMapping("/file")
    public ResponseEntity deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

        Integer delResult = cloudService.delete(1L, filename);

        ResponseEntity response = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (delResult == 200) {
            return ResponseEntity.ok().build();

        } else if (delResult == 400) {
            Map<String, ?> body = Map.of("message", "Error input data", "id", 1);
            response = new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);

        } else if (delResult == 401) {
            Map<String, ?> body = Map.of("message", "Unauthorized error", "id", 1);
            response = new ResponseEntity<>(body, headers, HttpStatus.UNAUTHORIZED);


        } else if (delResult == 500) {
            Map<String, ?> body = Map.of("message", "Error delete file", "id", 1);
            response = new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }


}
