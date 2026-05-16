package ru.netology.diploma.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.diploma.dto.ResponseFileEntity;
import ru.netology.diploma.exception.BaseDataAccessException;
import ru.netology.diploma.exception.ErrorInputDataException;

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
        //Logger.getLogger("getAllFiles, repository").log(Level.INFO,answerList.toString() );

        //if (!answerList.isEmpty()) {
        return answerList;
        //} else {
        //    throw new EmptyListOfFilesException("Список файлов для пользователя пуст");
        //}

    }

    public Boolean isSuccessAuthorization(Map<String, String> authData) {

        String login = authData.get("login");
        String password = authData.get("password");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", login)
                .addValue("password", password);

        String sql = "SELECT COUNT(*) FROM public.users WHERE email = :email AND pass = :password";
        Integer rows = jdbcTemplate.queryForObject(sql, params, Integer.class);

        return (rows > 0) ? true : false;

    }

    public void save(Long userId, String filename, byte[] fileBytes) {

//        Map<String, Serializable> params = Map.of(
//                "userid", userId,
//                "filename", filename,
//                "filedata", fileBytes
//        );

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
            //Logger.getLogger("TryDelete").log(Level.WARNING,"section-try");
        } catch (DataAccessException e) {
            //Logger.getLogger("TryDelete").log(Level.WARNING,"section-cach-500");
            throw new BaseDataAccessException(e.getMessage());
            //return 500; //"Error delete file"
        }

        if (rowAffected == 0) {
            ///without database error
            throw new ErrorInputDataException("error input data");
            //return 400; //"Error input data"
//        } else {
//            return 200; //"Success deleted"
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
