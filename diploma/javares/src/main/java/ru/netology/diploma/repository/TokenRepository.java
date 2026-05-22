package ru.netology.diploma.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.diploma.model.AuthResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TokenRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Map<String, Long> activeTokens = new ConcurrentHashMap<>();

    public TokenRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuthResponse isSuccessAuthorization(Map<String, String> authData) {

       final AuthResponse authResponse = new AuthResponse();

        String login = authData.get("login");
        String password = authData.get("password");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", login)
                .addValue("password", password);

        String sql1 = "SELECT COUNT(*) FROM public.users WHERE email = :email AND pass = :password";
        Integer rows = jdbcTemplate.queryForObject(sql1, params, Integer.class);

        Boolean succAuth = (rows > 0) ? true : false;

        if (!succAuth) {
            authResponse.setUserId(0L);
            authResponse.setSuccAuth(false);
            return authResponse;
        }

        String sql2 = "SELECT id FROM public.users WHERE email = :email AND pass = :password LIMIT 1";
        List<Long> userIdList = jdbcTemplate.query(sql2, params, (rs, rowNum) -> {
            Long userId = rs.getLong("id");
            return userId;
        });

        Long userId = userIdList.getFirst();
        authResponse.setUserId(userId);
        authResponse.setSuccAuth(true);

        return authResponse;
    }

    public void saveToken(Long userId, String userToken) {
        activeTokens.put(userToken, userId);
    }

    public Optional<Long> findToken(String userToken) {
        return Optional.ofNullable(activeTokens.get(userToken));
    }

    public void deleteToken(String userToken) {
        activeTokens.remove(userToken);
    }

    public Long getUserIdByToken(String authToken) {
        return activeTokens.get(authToken);


    }
}
