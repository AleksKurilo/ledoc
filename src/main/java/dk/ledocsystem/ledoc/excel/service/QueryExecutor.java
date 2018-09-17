package dk.ledocsystem.ledoc.excel.service;

import dk.ledocsystem.ledoc.excel.model.Row;
import dk.ledocsystem.ledoc.excel.model.Sheet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class QueryExecutor {

    @Resource
    private JdbcTemplate jdbcTemplate;

    List<Row> mapRows(Sheet sheet) {
        return jdbcTemplate.query(sheet.getQuery(), sheet.getParams(), new InnerMapper());
    }

    public static class InnerMapper implements RowMapper<Row> {
        @Nullable
        @Override
        public Row mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            int columnNum = rs.getMetaData().getColumnCount();
            Row row = new Row();
            for (int i = 1; i<=columnNum; i++) {
                row.getValues().add(rs.getObject(i));
            }
            return row;
        }
    }
}
