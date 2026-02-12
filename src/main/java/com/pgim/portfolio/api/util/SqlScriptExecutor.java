package com.pgim.portfolio.api.util;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StreamUtils;

public class SqlScriptExecutor {
    public static void executeSqlScript(JdbcTemplate jdbcTemplate, String sqlFile) {
        try {
            Resource resource = new ClassPathResource(sqlFile);
                String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        jdbcTemplate.execute(trimmed);
                    }
                }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL script: " + sqlFile, e);
        }
    }
}