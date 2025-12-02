package edu.yandex.project.repository.jdbc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.yandex.project.entity.TagEntity;
import edu.yandex.project.exception.GeneralProjectException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

@Slf4j
@UtilityClass
public class DbUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static Array convertToNativePostgreSqlTextArray(@NonNull JdbcTemplate jdbcTemplate,
                                                           @NonNull Collection<String> toBeConverted) {
        return jdbcTemplate.execute((ConnectionCallback<Array>) connection -> {
            log.debug("DbUtil::convertToNativePostgreSqlTextArray {}", toBeConverted);
            try {
                var array = connection.createArrayOf("text", toBeConverted.toArray());
                log.debug("DbUtil::convertToNativePostgreSqlTextArray {} created successfully", array);
                return array;
            } catch (SQLException | NullPointerException exc) {
                log.error("DbUtil::convertToNativePostgreSqlTextArray FAILED ({})", exc.getLocalizedMessage());
                throw new GeneralProjectException(exc.getMessage());
            }
        });
    }

    public static Set<TagEntity> getTags(@NonNull ResultSet rs) {
        try {
            return OBJECT_MAPPER.readValue(
                    rs.getString("p_tags_json_array"),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(Set.class, TagEntity.class)
            );
        } catch (SQLException | JsonProcessingException exc) {
            throw new GeneralProjectException(exc.getLocalizedMessage());
        }
    }

    public static boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
