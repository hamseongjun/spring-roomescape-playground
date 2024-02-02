package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.controller.dto.CreateTimeDto;
import roomescape.domain.Time;
import roomescape.exception.NotFoundTimeException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class TimeDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public TimeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("time")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Time> timeRowMapper = (rs, rn) -> new Time(
            rs.getLong("id"),
            rs.getTime("time").toLocalTime()
    );

    public List<Time> findAllTimes() {
        String sql = "select id, time from time";
        return jdbcTemplate.query(sql, timeRowMapper);
    }

    public Time save(CreateTimeDto dto) {
        Map<String, Object> params = new HashMap<>();
        params.put("time", dto.getTime());

        Number key = jdbcInsert.executeAndReturnKey(params);
        long savedId = Objects.requireNonNull(key).longValue();

        return new Time(savedId, dto.getTime());
    }

    public void delete(Long id) {
        String sql = "delete from time where id = ?";
        int count = jdbcTemplate.update(sql, id);

        if (count == 0) throw new NotFoundTimeException();
    }
}