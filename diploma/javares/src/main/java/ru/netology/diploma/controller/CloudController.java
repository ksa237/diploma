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
import ru.netology.diploma.exception.UnauthorizedException;
import ru.netology.diploma.service.AuthService;
import ru.netology.diploma.service.CloudService;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/")
public class CloudController {

    //класс контроллера - здесь отлавливаем ВСЕ эндпойнты которые заявлены в документации к API

    private final CloudService cloudService;
    private final AuthService authService;

    public CloudController(CloudService cloudService, AuthService authService) {
        this.cloudService = cloudService;
        this.authService = authService;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //++ scope AuthController
    // в данной области кода выполняется извлечение логина и пароля пришедшего в запросе
    // проверка авторизационных данных по базе пользователей - нужно обратиться в репозиторий
    // и как успешный результат выдача токена либо возврат отказа в полномочиях
    // также предусматривается проверка валидности токена, например
    // выдачу, проверку, хранение, удаление токена предусматривается в специальном классем TokenRepository
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authorizationMethod(@RequestBody Map<String, String> authData) {

        //только здесь в запросе придодит json структура с именем пользоввателя и паролем, извлекаем стуктуру из запроса (веб)
        //отправляем в бд на пердмет проверки существоввания такого пользователя и правильности пароля.

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


        //здесь проходит процесс авторизации и если она успешна, то возвращается сгенерированный токен
        //если что-то нас неустраивает, неправильно введено, не существует, тогда генерируется в authService исключение BadCredentials
        String userToken = authService.processAuthorization(authData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("auth-token", userToken);

        //возврат успешного ответа, код 200
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(body);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutMethod(@RequestHeader("auth-token") String authToken) {
        // kill auth-token

        //токен нужно удалить, так как пользователь вышел с фронтенд -системы
        authService.invalidateToken(authToken);

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

        //здесь вызывается функция загрузки файла пользоваетеля на сервер
        //в запросе от клиента мы ожидаем объект по схеме API, у которого два поля
        //--file
        //--hash
        // именно в file созержаться двоичные данные закачиваемого файла пользователем его будем получать через
        //StandardServletMultipartResolver

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
        //проверяем токен
        if (!authService.isValidToken(authToken)) {
            throw new UnauthorizedException("Невалидный токен");
        }

        Enumeration<String> headerNames = request.getHeaderNames();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //здесь проверяем в запросе наличие нужного типа данных
        //если запрос приходит с другим типом данных для нас это ошибочный запрос - исключение ErrorInputDataException
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

        //также проверим структуру запроса, если отсутствует заголовок с указанием типа содержимого - выбросим ошибку
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
                fileBytes = file.getBytes(); //требует обработку IOException
                existContent = true;
            } catch (IOException e) {
                throw new BaseIOException(e.getMessage());
            }
        }

        //если данные файла успешно извлечены из запроса и файл не пуст - считаем успехом и отправляем в БД для сохранения, иначе - исключение согласно API-doc
        if (existContent) {
            Long userId = authService.getUserIdByToken(authToken);
            //Logger.getLogger("CloudController").log(Level.WARNING,"uploadFileToServer >>> Long userId ="+userId.toString());
            //при работе с базой данных нам всегда нужно знать для какого пользователя выполняются операции в БД.
            //ИД пользоваетля мы храним рядом с выданным токеном и получаем его из репозитория токена, чтобы передать в работу в БД.
            cloudService.save(userId, filename, fileBytes);
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

        //проверяем токен
        if (!authService.isValidToken(authToken)) {
            throw new UnauthorizedException("Невалидный токен");
        }

        //получаем ИД пользоваетля для операции удаления файла пользоваетля в БД
        Long userId = authService.getUserIdByToken(authToken);
        cloudService.actionDelete(userId, filename);

        //200
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> dowloadFileFromCloud(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

        //здесь происходит загрузка файла пользователя с сервера и передача на клиент для сохранения на устройстве пользователя.

        //проверяем токен
        if (!authService.isValidToken(authToken)) {
            throw new UnauthorizedException("Невалидный токен");
        }

        //получаем через репозиторий файл нашего пользователя с userId
        Long userId = authService.getUserIdByToken(authToken);
        byte[] fileBytes = cloudService.get(userId, filename);


        //'#/components/schemas/File'
        //File:
        //    type: object
        //    properties:
        //        hash:
        //            type: string
        //        file:
        //            type: string
        //            format: binary

        //Данные фацйла храняться в БД в двоичном виде - именно такие данные и нужно передать в веб-ответ,
        // данные будут пердставлять собой поток байтов, но обернем этот поток дополнительно в буфер,
        //т.е. буферизированный поток
        InputStream data = new ByteArrayInputStream(fileBytes);
        BufferedInputStream buffData = new BufferedInputStream(data, 16 * 1024);
        InputStreamResource responseData = new InputStreamResource(buffData);

        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(filename).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); //подходит для данных вида двоичные данные файлов
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseData);
    }

    @PutMapping("/file")
    public ResponseEntity<String> editFileName(@RequestHeader("auth-token") String authToken, @RequestParam String filename, @RequestBody Map<String, String> updateData) {

        //здесь происходит переименование файла
        //текущее имя фала берем из параметра строки запроса в браузере, а новое имя файла из тела
        //запроса, поле .name

        //!!!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //Важное замечание: данный функционал успешно отрабатывает через Postman
        //однако на клиенте в браузере при нажатии кнопки, отвечающей за редактирование имени файла
        //не происходит какого либо вызова позволяющего задать НОВОЕ имя файла для переименования,
        //а происходит запрос на фронтенд, который разумеется завершается неудачей, так как новое имя файла =null
        //!!!<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

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

        //'200': Success upload
        //'400': Error input data
        //'401': Unauthorized error
        //'500': Error Edit file name

        //проверяем токен
        if (!authService.isValidToken(authToken)) {
            throw new UnauthorizedException("Невалидный токен");
        }

        String newfilename = updateData.get("name");
        Long userId = authService.getUserIdByToken(authToken); //вычислим ИД нашего пользователя для выполнения операций над базой данных
        cloudService.editFileName(userId, filename, newfilename);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();

    }

    @GetMapping("/list")
    public ResponseEntity<List<ResponseFileEntity>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam Integer limit) {

        //здесь происходит возврат списка файлов нашего пользователя

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


        //проверяем токен
        if (!authService.isValidToken(authToken)) {
            throw new UnauthorizedException("Невалидный токен");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Long userId = authService.getUserIdByToken(authToken); //вычислим ИД нашего пользователя для выполнения операций над базой данных
        //вызов сервиса, здесь получаем осноные данные для возврата на клиент
        List<ResponseFileEntity> bodyList = cloudService.getAllFiles(userId, limit);

        //возврат успешного ответа, код 200
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(bodyList);

    }


}
