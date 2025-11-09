package edu.yandex.project.integration.config;

import edu.yandex.project.config.AppConfig;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan(
        basePackages = "edu.yandex.project"

)
@Configuration
@PropertySource("classpath:application-test.properties")
public class AppITConfig extends AppConfig {
}
