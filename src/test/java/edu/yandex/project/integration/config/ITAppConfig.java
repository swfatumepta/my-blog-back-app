package edu.yandex.project.integration.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan(
        basePackages = {"edu.yandex.project", "edu.yandex.project.integration.config"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "edu.yandex.project.config.*"
        )
)
@Configuration
@EnableWebMvc
@PropertySource("classpath:application-test.properties")
public class ITAppConfig implements WebMvcConfigurer {
}
