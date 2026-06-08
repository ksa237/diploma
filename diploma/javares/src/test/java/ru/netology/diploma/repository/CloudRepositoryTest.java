package ru.netology.diploma.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
class CloudRepositoryTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    CloudRepositoryTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }


    @Test
    @Disabled
    void createDataTest() throws InterruptedException {

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(100))");
        jdbcTemplate.update("INSERT INTO users (name) VALUES(?)", "Elena K");
        Thread.sleep(10000);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertThat(count).isEqualTo(1);

    }


    @Test
    void getAllFiles_return_dataAccessException() {

        jdbcTemplate.execute("DROP TABLE IF EXISTS public.users CASCADE");

        String sqlCreateUs ="create table IF NOT EXISTS public.users(\n" +
                "id bigserial not null,\n" +
                "email varchar(50) not null,\n" +
                "pass varchar(20) not null,\n" +
                "\n" +
                "CONSTRAINT pk_users PRIMARY KEY (email)\n" +
                ");";
        jdbcTemplate.execute(sqlCreateUs);
        jdbcTemplate.update("INSERT INTO users (email, pass) VALUES(?,?)","lena@mail.ru","123456");

        jdbcTemplate.execute("DROP TABLE IF EXISTS public.userfiles CASCADE");
        String sqlCreateFl = "create table IF NOT EXISTS public.userfiles(\n" +
                "id bigserial not null,\n" +
                "userid bigint not null,\n" +
                "filename varchar(50) not null,\n" +
                "filedata bytea not null,\n" +
                "\n" +
                "CONSTRAINT pk_userfiles PRIMARY KEY(filename)\n" +
                ");\n";

        byte[] simpleF = "якупилженеидочкеразноцветныечулочки".getBytes();

        jdbcTemplate.execute(sqlCreateFl);
        jdbcTemplate.update("INSERT INTO public.userfiles (userid, filename, filedata) VALUES (?, ?, ?)",1L,"report.pdf", simpleF);


        //String sql = "SELECT filename, octet_length(filedata) AS size FROM public.userfiles WHERE userid = ? LIMIT ?";

       Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public.userfiles WHERE userid = ?", Integer.class, 1L);

       assertThat(count).isEqualTo(1);


    }
}

class ListOfFilesReturn {

    private String filename;
    private Long size;

    public ListOfFilesReturn() {

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}