package edu.yandex.project.integration.config;

import edu.yandex.project.factory.PostFactory;
import edu.yandex.project.repository.CommentRepository;
import edu.yandex.project.repository.PostRepository;
import edu.yandex.project.repository.TagRepository;
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
    protected PostFactory mockedPostFactory() {
        return Mockito.mock(PostFactory.class);
    }

    @Bean
    protected PostRepository mockedPostRepository() {
        return Mockito.mock(PostRepository.class);
    }

    @Bean
    protected CommentRepository mockedCommentRepository() {
        return Mockito.mock(CommentRepository.class);
    }

    @Bean
    protected TagRepository mockedTagRepository() {
        return Mockito.mock(TagRepository.class);
    }
}
