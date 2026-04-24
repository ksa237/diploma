package ru.netology.diploma.repository;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.HashMap;
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

        Map<String, String> params = Map.of("email", login, "password", password);
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
        String sql = "INSERT INTO public.userfiles (userid, filename, filedata) VALUES (:userid, :filename, :filedata)";
       jdbcTemplate.update(sql, params);

    }

    public Integer delete(Long userId, String filename) {

        String sql = "DELETE FROM public.userfiles WHERE filename = :filename AND userid = :userid";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filename", filename)
                .addValue("userid", userId);

        Integer rowAffected =0;
        try {
            rowAffected = jdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            return 500; //"Error delete file"
        }

        if(rowAffected == 0){
            ///without database error
            return 400; //"Error input data"
        }else {
            return 200; //"Success deleted"
        }


    }
}
