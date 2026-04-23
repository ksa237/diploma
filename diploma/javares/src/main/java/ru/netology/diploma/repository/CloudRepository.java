package ru.netology.diploma.repository;


import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class CloudRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CloudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getAllFiles(Long userId) {

        Map params = Map.of("param1", "value1");
        String sql = "SELECT CURRENT_TIMESTAMP current_ts;";

        List<String> answer = jdbcTemplate.query(sql, (rs, rowNum) -> {
            String currentTS = rs.getString("current_ts");
            return currentTS;
        });

        return answer;
    }

    public Boolean isSuccessAuthorization(Map<String, String> authData) {

        String login = authData.get("login");
        String password = authData.get("password");

        Map params = Map.of("email", login, "password", password);
        String sql = "SELECT COUNT(*) FROM public.users WHERE email = :email AND pass = :password";
        Integer rows = jdbcTemplate.queryForObject(sql, params, Integer.class);

        return (rows > 0) ? true : false;

    }

    public void save(Long userId, String filename, byte[] fileBytes) {

        Map<String, Serializable> params = Map.of(
                "userid", userId,
                "filename", filename,
                "filedata", fileBytes
        );
        String sql = "INSERT INTO public.UserFiles (userid, filename, filedata) VALUES (:userid, :filename, :filedata)";
       jdbcTemplate.update(sql, params);

    }
}
