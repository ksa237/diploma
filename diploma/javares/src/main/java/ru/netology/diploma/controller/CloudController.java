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

/// класс контроллера - здесь отлавливаем ВСЕ эндпойнты которые заявлены в документации к API
@RestController
@RequestMapping("/")
public class CloudController {

    private final CloudService cloudService;
    private final AuthService authService;

    public CloudController(CloudService cloudService, AuthService authService) {
        this.cloudService = cloudService;
        this.authService = authService;
    }

    /// в данной области кода выполняется извлечение логина и пароля пришедшего в запросе
    /// проверка авторизационных данных по базе пользователей - нужно обратиться в репозиторий
    /// и как успешный результат выдача токена либо возврат отказа в полномочиях
    /// также предусматривается проверка валидности токена, например
    /// выдачу, проверку, хранение, удаление токена предусматривается в специальном классем TokenRepository
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authorizationMethod(@RequestBody Map<String, String> authData) {
        //только здесь в запросе придодит json структура с именем пользоввателя и паролем, извлекаем стуктуру из запроса (веб)
        //отправляем в бд на пердмет проверки существоввания такого пользователя и правильности пароля.

        String userToken = authService.processAuthorization(authData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("auth-token", userToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(body);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutMethod(@RequestHeader("auth-token") String authToken) {

        //токен нужно удалить, так как пользователь вышел с фронтенд -системы
        authService.invalidateToken(authToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers).build();

    }

    /// здесь вызывается функция загрузки файла пользоваетеля на сервер
    /// в запросе от клиента мы ожидаем объект по схеме API, у которого два поля
    /// --file
    /// --hash
    /// именно в file созержаться двоичные данные закачиваемого файла пользователем его будем получать через StandardServletMultipartResolver
    @PostMapping("/file")
    public ResponseEntity<String> uploadFileToServer(@RequestHeader("auth-token") String authToken, @RequestParam String filename, HttpServletRequest request) {

        //проверить auth-token, если false тогда  401 - Unauthorized error
        if (!authService.isValidToken(authToken)) {
            throw new UnauthorizedException("Невалидный токен");
        }
        checkContentType(request);

        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);

        byte[] fileBytes = checkExistContent(multipartRequest);

        //если данные файла успешно извлечены из запроса и файл не пуст - считаем успехом и отправляем в БД для сохранения, иначе - исключение согласно API-doc
        Long userId = authService.getUserIdByToken(authToken);

        cloudService.save(userId, filename, fileBytes);

        String hash = multipartRequest.getParameter("hash"); // а дальше что с ним делать? ;-)

        //200
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();

    }

    private byte[] checkExistContent(MultipartHttpServletRequest multipartRequest) {

        byte[] fileBytes;
        MultipartFile file = multipartRequest.getFile("file");
        if (file != null && !file.isEmpty()) {
            try {
                fileBytes = file.getBytes(); //требует обработку IOException
            } catch (IOException e) {
                throw new BaseIOException(e.getMessage()); //500
            }
        } else {
            throw new ErrorInputDataException("File data is empty");
        }

        return fileBytes;

    }

    /// здесь проверяем в запросе наличие нужного типа данных
    /// если запрос приходит с другим типом данных для нас это ошибочный запрос - исключение ErrorInputDataException
    private void checkContentType(HttpServletRequest request) {

        Enumeration<String> headerNames = request.getHeaderNames();

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

    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

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

    /// здесь происходит загрузка файла пользователя с сервера и передача на клиент для сохранения на устройстве пользователя.
    /// Данные фацйла храняться в БД в двоичном виде - именно такие данные и нужно передать в веб-ответ,
    /// данные будут пердставлять собой поток байтов, обернутые дополнительно в буфер, т.е. буферизированный поток
    @GetMapping("/file")
    public ResponseEntity<Resource> dowloadFileFromCloud(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {

        //проверяем токен
        if (!authService.isValidToken(authToken)) {
            throw new UnauthorizedException("Невалидный токен");
        }

        //получаем через репозиторий файл нашего пользователя с userId
        Long userId = authService.getUserIdByToken(authToken);
        byte[] fileBytes = cloudService.get(userId, filename);

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

    /// здесь происходит переименование файла
    /// текущее имя фала берем из параметра строки запроса в браузере, а новое имя файла из тела
    /// запроса, поле .name
    ///
    /// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /// Важное замечание: данный функционал успешно отрабатывает через Postman
    /// однако на клиенте в браузере при нажатии кнопки, отвечающей за редактирование имени файла
    /// не происходит какого либо вызова позволяющего задать НОВОЕ имя файла для переименования,
    /// а происходит запрос на фронтенд, который разумеется завершается неудачей, так как новое имя файла =null
    /// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @PutMapping("/file")
    public ResponseEntity<String> editFileName(@RequestHeader("auth-token") String authToken, @RequestParam String filename, @RequestBody Map<String, String> updateData) {

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

    /// здесь происходит возврат списка файлов нашего пользователя
    @GetMapping("/list")
    public ResponseEntity<List<ResponseFileEntity>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam Integer limit) {

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
