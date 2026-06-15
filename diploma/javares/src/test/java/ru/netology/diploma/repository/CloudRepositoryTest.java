package ru.netology.diploma.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

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
    private final DatabaseInitializer databaseInitializer;


    @Autowired
    CloudRepositoryTest(JdbcTemplate jdbcTemplate, DatabaseInitializer databaseInitializer) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseInitializer = databaseInitializer;
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void setUp() {
        databaseInitializer.init(jdbcTemplate);
    }


    @Test
    void getAllFiles_test() {

        String sql = "SELECT filename, octet_length(filedata) AS size FROM public.userfiles WHERE userid = ? LIMIT ?";

        List<FileEntityTest> userFiles = jdbcTemplate.query(sql, new Object[]{1L, 3}, (rs, rowNum) -> {
            FileEntityTest fileEn = new FileEntityTest();
            fileEn.setFilename(rs.getString("filename"));
            fileEn.setSize(rs.getLong("size"));
            return fileEn;
        });

        assertThat(userFiles).hasSizeGreaterThan(0);

    }

    @Test
    void actionDelete_test() {

        String testFileName = "crybabies.png";
        String sql = "DELETE FROM public.userfiles WHERE filename = ? AND userid = ?";
        Integer rowAffected = jdbcTemplate.update(sql, testFileName, 1L);

        assertThat(rowAffected).isEqualTo(1);

    }

    @Test
    void save_test() {

        Long testUserId = 3L;
        String testFilename = "java_tutorial2.rtf";
        byte[] testFileBytes = "bytes_from_file: java_tutorial2.rtf".getBytes();
        String sql = "INSERT INTO public.userfiles (userid, filename, filedata) VALUES (?, ?, ?)";
        Integer rowAffected = jdbcTemplate.update(sql, testUserId, testFilename, testFileBytes);

        assertThat(rowAffected).isEqualTo(1);

    }


}

class FileEntityTest {

    private String filename;
    private Long size;

    public FileEntityTest() {

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