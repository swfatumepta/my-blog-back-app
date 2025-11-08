package edu.yandex.project.integration.config;

import edu.yandex.project.config.DataSourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class ITDataSourceConfig extends DataSourceConfig {
}
