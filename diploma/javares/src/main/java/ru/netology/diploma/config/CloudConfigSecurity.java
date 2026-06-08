package ru.netology.diploma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CloudConfigSecurity {


    ///здесь настраивается конфигурация безопасности SpringSecurity через установку фильтров безопасности
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //отключим, чтобы разрешить метод POST
                .authorizeHttpRequests((authz) -> authz //настраиваем правила доступа к путям URL
                        .requestMatchers("/list").permitAll() // разрешено без авторизации
                        .requestMatchers("/login").permitAll() // разрешено без авторизации
                        .requestMatchers("/logout").permitAll() // разрешено без авторизации
                        .requestMatchers("/file").permitAll() // разрешено без авторизации

                )

                //в целом мне пришлось отказаться от механизма SpringSecirity, так как на любой эндпойнт этот механизм будет
                //проверять кому (каким ролям hasRole) разрешено переходить по этой странице
                //но в нашей системе предполагается система когда фронтэнд будет отправлять в каждом запросе токен, который и
                //будет являтся результатом успешной авторизации и аутентификации, а сами пользователи будут хнариться в БД.
                //таки образом получается проверка прав доступа будет проходить не средствами SpringSecurity а собственными средствами
                //используя auth-token который приходит в каждом запросе кроме запрроса на /login


                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

                //я отключил формы login и logout от SpringSecirity, так как они "выскакивают" с предожением ввести имя пользователя и пароль,
                //однако такая форма у нас есть на фронтэнд - ее и используем.

        return http.build();
    }

}



