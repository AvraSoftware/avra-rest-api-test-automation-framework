package com.avra.qa.secondmodulename.group.util.jpa;

import com.avra.qa.common.util.datautil.postgres.QueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class GroupRepository {

    private static final String SCHEMA_NAME_GROUP = "schema_name.group";
    private static final String DELETE_GROUP = "DELETE FROM schema_name.group;";
    private static final String SELECT_GROUP = "SELECT * FROM schema_name.group WHERE id = :id;";
    private static final String SELECT_ALL_ACTIVE_GROUPS = "SELECT * FROM schema_name.group WHERE active = true;";

    @Autowired
    private QueryHelper queryHelper;

    public Map<String, String> getGroup(UUID id) {
        queryHelper.waitForDatabaseRow(SCHEMA_NAME_GROUP,
                "id", id.toString());

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return queryHelper.getQueryResultAsMap(SELECT_GROUP, params);
    }

    public List<Map<String, String>> getAllActiveGroups(String active) {
        queryHelper.waitForDatabaseRow(SCHEMA_NAME_GROUP,
                "active", active);

        MapSqlParameterSource params = new MapSqlParameterSource("active", active);
        return queryHelper.getQueryResultAsListMap(SELECT_ALL_ACTIVE_GROUPS, params);
    }

    public void deleteAllGroups() {
        queryHelper.executeUpdate(DELETE_GROUP);
    }
}
