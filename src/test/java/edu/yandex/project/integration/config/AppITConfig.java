package edu.yandex.project.integration.config;

import edu.yandex.project.config.DataSourceConfig;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ComponentScan(
        basePackages = "edu.yandex.project.*",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "edu.yandex.project.config.*")
)
@Configuration
@EnableWebMvc
@Import(DataSourceConfig.class)
public class AppITConfig {
}
