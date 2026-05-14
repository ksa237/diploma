package ru.netology.diploma.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import ru.netology.diploma.dto.ResponseFileEntity;
import ru.netology.diploma.exception.BaseIOException;
import ru.netology.diploma.exception.ErrorInputDataException;
import ru.netology.diploma.service.CloudService;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    /// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //++ scope AuthController
    // в данной области кода выполняется извлечение логина и пароля пришедшего в запросе
    // проверка авторизационных данных по базе пользователей - нужно обратиться в репозиторий
    // и как успешный результат выдача токена либо возврат отказа в полномочиях
    // также предусматривается проверка валидности токена, например удовлетворение сроку действия токена
    // выдачу, проверку, хранение, удаление токена предусматривается в специальном классем TokenRepository
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authorizationMethod(@RequestBody Map<String, String> authData) throws IOException {

        //200
        //Login:
        //type: object
        //    properties:
        //        auth-token:
        //        type: string

        //400
        //Error:
        //type: object
        //  properties:
        //      message:
        //          type: string
        //          description: Error message
        //      id:
        //          type: integer

        cloudService.processAuthorization(authData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("auth-token", "my-token-manafaka");

        //возврат успешного ответа, код 200
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(body);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutMethod(@RequestHeader("auth-token") String authToken) {
        // kill auth-token

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers).build();


    }
    //-- scope AuthController
    /// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @PostMapping("/file")
    public ResponseEntity<String> uploadFileToServer(@RequestHeader("auth-token") String authToken, @RequestParam String filename, HttpServletRequest request) {

        //400 - Error input data
        //401 - Unauthorized error
        //$ref: '#/components/schemas/Error'
        //Error:
        //type: object
        //----properties:
        //--------message:
        //------------type: string
        //------------description: Error message
        //--------id:
        //------------type: integer


        //проверить auth-token, если false тогда  401 - Unauthorized error


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
                    throw new ErrorInputDataException("Invalid content type");
                }
            }
        }

        if (!existContentType) {
            throw new ErrorInputDataException("No header Content-Type");
        }


        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);

        Boolean existContent = false;
        byte[] fileBytes = null;
        MultipartFile file = multipartRequest.getFile("file");
        if (file != null && !file.isEmpty()) {
            try {
                fileBytes = file.getBytes();
                existContent = true;
            } catch (IOException e) {
                throw new BaseIOException(e.getMessage());
            }
        }

        if (existContent) {
            cloudService.save(1L, filename, fileBytes);
        } else {
            throw new ErrorInputDataException("File data is empty");

        }

        String hash = multipartRequest.getParameter("hash"); // а дальше что с ним делать? ;-)


        //200
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();

    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

        //400 - error input data
        //401 - unathorized error
        //500 - error delete file

        cloudService.actionDelete(1L, filename);

//        ResponseEntity<?> response = null;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        if (delResult == 200) {
//            return ResponseEntity.ok().build();
//
//        } else if (delResult == 400) {
//            //'#/components/schemas/Error'
//            Map<String, ?> body = Map.of("message", "Error input data", "id", 1);
//            response = new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
//
//        } else if (delResult == 401) {
//            //'#/components/schemas/Error'
//            Map<String, ?> body = Map.of("message", "Unauthorized error", "id", 1);
//            response = new ResponseEntity<>(body, headers, HttpStatus.UNAUTHORIZED);
//
//
//        } else if (delResult == 500) {
//            //'#/components/schemas/Error'
//            Map<String, ?> body = Map.of("message", "Error delete file", "id", 1);
//            response = new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
//        }

        //200
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> dowloadFileFromCloud(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

        byte[] fileBytes = cloudService.get(1L, filename);


        //'#/components/schemas/File'
        //File:
        //    type: object
        //    properties:
        //        hash:
        //            type: string
        //        file:
        //            type: string
        //            format: binary


        InputStream data = new ByteArrayInputStream(fileBytes);
        BufferedInputStream buffData = new BufferedInputStream(data, 16 * 1024);
        InputStreamResource responseData = new InputStreamResource(buffData);

        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(filename).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseData);
    }

    @PutMapping("/file")
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

        //requestBody:
        //description: Login and password hash
        //required: true
        //content:
        //application/json:
        //schema:
        //type: object
        //properties:
        //name:
        //type: string

        return ResponseEntity.ok().build(); // 200

    }

    @GetMapping("/list")
    public ResponseEntity<List<ResponseFileEntity>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam Integer limit) {

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

        //вызов сервиса, здесь получаем осноные данные для возврата на клиент
        List<ResponseFileEntity> bodyList = cloudService.getAllFiles(1L, limit);

        //возврат успешного ответа, код 200
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(bodyList);

        //ok(bodyList, headers, HttpStatus.OK);
        //return response;
        //ResponseEntity.ok().build(); // 200
    }


}
