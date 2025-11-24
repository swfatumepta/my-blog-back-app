package edu.yandex.project.integration.config;

import edu.yandex.project.config.DataSourceConfig;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan("edu.yandex.project.repository.jdbc")
@Configuration
@EnableTransactionManagement
@Import(DataSourceConfig.class)
public class JdbcRepoITConfig {
}
