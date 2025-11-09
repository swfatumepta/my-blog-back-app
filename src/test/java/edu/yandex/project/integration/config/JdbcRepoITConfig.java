package edu.yandex.project.integration.config;

import edu.yandex.project.config.DataSourceConfig;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan(
        basePackages = "edu.yandex.project.repository.jdbc",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "edu.yandex.project.config.*")
)
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application-test.properties")
public class JdbcRepoITConfig extends DataSourceConfig {
}
