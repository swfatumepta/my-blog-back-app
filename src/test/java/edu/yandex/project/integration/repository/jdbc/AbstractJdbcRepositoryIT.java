package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.integration.AbstractDbIT;
import edu.yandex.project.integration.config.JdbcRepoITConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(JdbcRepoITConfig.class)
public class AbstractJdbcRepositoryIT extends AbstractDbIT {
}
