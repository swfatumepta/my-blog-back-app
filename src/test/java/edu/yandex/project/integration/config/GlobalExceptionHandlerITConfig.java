package edu.yandex.project.integration.config;

import edu.yandex.project.factory.PostFactory;
import edu.yandex.project.repository.PostRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = {
        "edu.yandex.project.controller",
        "edu.yandex.project.service",
        "edu.yandex.project.exception"
})
@EnableWebMvc
@Profile("exception")
public class GlobalExceptionHandlerITConfig {

    @Bean
    protected PostFactory postFactory() {
        return Mockito.mock(PostFactory.class);
    }

    @Bean
    protected PostRepository postRepository() {
        return Mockito.mock(PostRepository.class);
    }
}
