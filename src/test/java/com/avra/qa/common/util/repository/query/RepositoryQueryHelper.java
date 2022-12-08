package com.avra.qa.common.util.repository.query;

import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Component
public class RepositoryQueryHelper {

    @Value("${timeout}")
    private int timeout;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Map<String, String> getQueryResultAsMap(String sql, MapSqlParameterSource params) {
        Map<String, String> result = new HashMap<>();
        namedParameterJdbcTemplate.queryForMap(sql, params)
                .forEach((key, value) -> result.put(key, String.valueOf(value)));
        return result;
    }

    public List<Map<String, Object>> getQueryResultAsListMap(String sql, MapSqlParameterSource params) {
        List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList(sql, params);
        result.forEach(rowMap -> rowMap.forEach((key, value) -> rowMap.put(key, String.valueOf(value))));
        return result;
    }

    public <T> T getQueryResultByResultSetExtractor(String sql, MapSqlParameterSource params, ResultSetExtractor<T> extractor) {
        ResultSetExtractor<T> wrapper = rs -> {
            if (rs.next()) {
                return extractor.extractData(rs);
            } else {
                return null;
            }
        };
        return namedParameterJdbcTemplate.query(sql, params, wrapper);
    }

    public List<String> getQueryResultLimitedToColumn(String sql, MapSqlParameterSource params, String columnName) {
        RowMapper<String> rowMapper = (rs, rowNum) -> rs.getObject(columnName).toString();
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    public List<Long> getQueryResultFromCount(String sql, MapSqlParameterSource params) {
        RowMapper<Long> rowMapper = (rs, rowNum) -> (Long) rs.getObject("count");
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    public List<Timestamp> getQueryResultLimitedToColumnAsTimestamp(String sql, MapSqlParameterSource params, String columnName) {
        RowMapper<Timestamp> rowMapper = (rs, rowNum) -> rs.getTimestamp(columnName);
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    public void waitForDatabaseRowWhereColumnIsEqualToValue(String sqlTable, String columnIdName, String id) {
        String selectSql = "SELECT count(*) FROM " + sqlTable + " WHERE " + columnIdName + " = :id;";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            await()
                    .atMost(timeout, TimeUnit.MILLISECONDS)
                    .pollInterval(15, TimeUnit.MILLISECONDS)
                    .until(() -> getQueryResultByResultSetExtractor(selectSql, params, rs -> rs.getInt(1)) > 0);
        } catch (ConditionTimeoutException e) {
            throw new RuntimeException("Database row not found: table: " + sqlTable + ", columnIdentifier: " + columnIdName + " , value:" + id);
        }
    }

    public void waitForDatabaseRowWhereColumnLike(String sqlTable, String columnIdName, String value) {
        String selectSql = "SELECT count(*) FROM " + sqlTable + " WHERE " + columnIdName + " LIKE :value;";
        MapSqlParameterSource params = new MapSqlParameterSource("value", "%" + value + "%");
        try {
            await()
                    .atMost(timeout, TimeUnit.MILLISECONDS)
                    .pollInterval(15, TimeUnit.MILLISECONDS)
                    .until(() -> getQueryResultByResultSetExtractor(selectSql, params, rs -> rs.getInt(1)) > 0);
        } catch (ConditionTimeoutException e) {
            throw new RuntimeException("Database row not found: table: " + sqlTable + ", columnIdentifier: " + columnIdName + " , value:" + value);
        }
    }

    public void waitForDatabaseUpdate(String sqlTable, String rowIdentifier, String columnName, String expectedValue) {
        String selectSql = "SELECT " + columnName + " FROM " + sqlTable + " WHERE :rowIdentifier;";
        MapSqlParameterSource params = new MapSqlParameterSource("rowIdentifier", rowIdentifier);
        try {
            await()
                    .atMost(timeout, TimeUnit.MILLISECONDS)
                    .pollInterval(15, TimeUnit.MILLISECONDS)
                    .until(() -> getQueryResultLimitedToColumn(selectSql, params, columnName).get(0).contains(expectedValue));
        } catch (ConditionTimeoutException e) {
            throw new RuntimeException("Database row not updated: table: " + sqlTable + ", identifier: " + rowIdentifier + ", column:" + columnName
                    + ", expectedValue:" + expectedValue + ", actualValue: " + getQueryResultLimitedToColumn(selectSql, params, columnName).get(0));
        }
    }

}
