package ru.netology.diploma.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    public void init(JdbcTemplate jdbcTemplate){


        jdbcTemplate.execute("DROP TABLE IF EXISTS public.users CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS public.userfiles CASCADE");

        String sqlCreateUs ="""
                create table IF NOT EXISTS public.users(
                id bigserial not null,
                email varchar(50) not null,
                pass varchar(20) not null,
                CONSTRAINT pk_users PRIMARY KEY (email)
                )""";

        jdbcTemplate.execute(sqlCreateUs);

        jdbcTemplate.update("INSERT INTO users (email, pass) VALUES(?,?)","polina@rambler.ru","123");
        jdbcTemplate.update("INSERT INTO users (email, pass) VALUES(?,?)","lena@mail.ru","123456");
        jdbcTemplate.update("INSERT INTO users (email, pass) VALUES(?,?)","ksa237@yandex.ru","54321!");


        String sqlCreateFl = """
                create table IF NOT EXISTS public.userfiles(
                id bigserial not null,
                userid bigint not null,
                filename varchar(50) not null,
                filedata bytea not null,
                CONSTRAINT pk_userfiles PRIMARY KEY(filename)
                )""";
        jdbcTemplate.execute(sqlCreateFl);

        byte[] polinaFile = "здесь_тестовые_байты_для_файла1_пользователя1_с_ИД=1Л".getBytes();
        jdbcTemplate.update("INSERT INTO public.userfiles (userid, filename, filedata) VALUES (?, ?, ?)",1L,"crybabies.png", polinaFile);

        byte[] lenaFile = "здесь_тестовые_байты_для_файла1_пользователя2_с_ИД=2Л".getBytes();
        jdbcTemplate.update("INSERT INTO public.userfiles (userid, filename, filedata) VALUES (?, ?, ?)",2L,"marketplaces.pdf", lenaFile);

        byte[] papaFile = "здесь_тестовые_байты_для_файла1_пользователя3_с_ИД=3Л".getBytes();
        jdbcTemplate.update("INSERT INTO public.userfiles (userid, filename, filedata) VALUES (?, ?, ?)",3L,"java_tutorial.rtf", papaFile);


    }

}
