package edu.yandex.project.integration.config;

import org.springframework.context.annotation.*;

@ComponentScan(basePackages = "edu.yandex.project")
@Configuration
@PropertySource("classpath:application-test.properties")
public class AppITConfig {
}
