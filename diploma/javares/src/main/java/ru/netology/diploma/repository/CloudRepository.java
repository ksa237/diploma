package ru.netology.diploma.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.diploma.dto.ResponseFileEntity;
import ru.netology.diploma.exception.BaseDataAccessException;
import ru.netology.diploma.exception.ErrorInputDataException;

import java.util.List;

//класс выполнения операций с базой данных: добавление файла пользователя , получени списка всех файлов пользователя
// сохранения файла пользователя  в БД, удаление файла пользователя, получение (скачивание) файла пользователя
// изменения имени файла пользователя

@Repository
public class CloudRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CloudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ResponseFileEntity> getAllFiles(Long userId, Integer limit) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userid", userId)
                .addValue("limit", limit);

        String sql = "SELECT filename, octet_length(filedata) AS size FROM public.userfiles WHERE userid = :userid LIMIT :limit";

        List<ResponseFileEntity> answerList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {

            String filename = rs.getString("filename");
            Integer size = rs.getInt("size");

            ResponseFileEntity answ = new ResponseFileEntity(filename, size);

            return answ;
        });

        return answerList;

    }

    public void save(Long userId, String filename, byte[] fileBytes) {


        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userid", userId)
                .addValue("filename", filename)
                .addValue("filedata", fileBytes);

        String sql = "INSERT INTO public.userfiles (userid, filename, filedata) VALUES (:userid, :filename, :filedata)";
        jdbcTemplate.update(sql, params);

    }

    public void actionDelete(Long userId, String filename) {

        String sql = "DELETE FROM public.userfiles WHERE filename = :filename AND userid = :userid";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filename", filename)
                .addValue("userid", userId);


        Integer rowAffected = 0;
        try {
            rowAffected = jdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            throw new BaseDataAccessException(e.getMessage()); // 500
        }

        if (rowAffected == 0) {
            ///without database error
            throw new ErrorInputDataException("error input data"); // 400
        }

    }

    public byte[] getFile(long userId, String filename) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userid", userId)
                .addValue("filename", filename);

        String sql = "SELECT filedata FROM public.userfiles WHERE userid = :userid AND filename = :filename LIMIT 1";

        List<byte[]> answerList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {

            byte[] answ = rs.getBytes("filedata");
            return answ;
        });

        return answerList.get(0);
//        if(!answerList.isEmpty()){
//            return answerList.getFirst();
//        } else {
//            return new Exception(e.)
//        }
    }

    public void editFileName(long userId, String filename, String newfilename) {

        String sql = "UPDATE public.userfiles SET filename = :newfilename WHERE filename = :filename AND userid = :userid";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filename", filename)
                .addValue("newfilename", newfilename)
                .addValue("userid", userId);

        Integer rowAffected = 0;
        try {
            rowAffected = jdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            throw new BaseDataAccessException(e.getMessage());
        }

        if (rowAffected == 0) {
            ///without database error
            throw new ErrorInputDataException("error input data");
        }


    }
}
