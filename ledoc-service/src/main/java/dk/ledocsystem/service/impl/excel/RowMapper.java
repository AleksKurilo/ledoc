package dk.ledocsystem.service.impl.excel;

import dk.ledocsystem.service.impl.excel.model.Row;
import dk.ledocsystem.service.impl.excel.model.Sheet;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
class RowMapper {

    private final JdbcTemplate jdbcTemplate;

    List<Row> mapRows(Sheet sheet) {
        return jdbcTemplate.query(sheet.getQuery(), sheet.getParams(), new InnerMapper());
    }

    private static class InnerMapper implements org.springframework.jdbc.core.RowMapper<Row> {
        @Nullable
        @Override
        public Row mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            int columnNum = rs.getMetaData().getColumnCount();
            Row row = new Row();
            for (int i = 1; i <= columnNum; i++) {
                row.getValues().add(rs.getString(i));
            }
            return row;
        }
    }
}
