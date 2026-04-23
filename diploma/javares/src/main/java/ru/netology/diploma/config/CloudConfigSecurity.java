package ru.netology.diploma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CloudConfigSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //отключим, чтобы разрешить метод POST
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/list").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/file").permitAll()
                )

                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

}



