package ru.netology.diploma.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CloudConfig implements WebMvcConfigurer {

    /// здесь настраиваем правила "общения" между ресурсами сети
    /// сервер tomcat у нас расположен по пути (http://localhost:8080)
    /// а ПО-клиент обращается с точки http://localhost:8081 , т.е. пользовватель из браузера будет инициировать запросы по другим путям,
    /// вызывая добавление файла, просмотр списка файлов, авторизаци и т.д. и эти запросы будут отправляться на "другие" пути
    /// http://localhost:8080/login, http://localhost:8080/file, http://localhost:8080/list
    /// клиентское ПО будет видеть эти "другие" пути, которые отличаются от своей точки http://localhost:8081/ поэтому будет запрашивать настройку CORS
    /// наш сервер ответит что :
    ///  - правила есть и касаются любый вложенных путей от http://localhost:8080/
    ///  - разрешено получать запросы с фронтенд - http://localhost:8081 , с других - запрещено, например http://localhost:8888
    ///  - разрешены все виды запросов GET, POST и т.д.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(
                        "http://localhost:8081"
                )
                .allowedMethods("*");


    }
}



