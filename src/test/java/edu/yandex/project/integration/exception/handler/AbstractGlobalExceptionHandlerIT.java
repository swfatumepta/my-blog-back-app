package edu.yandex.project.integration.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.yandex.project.controller.CommentController;
import edu.yandex.project.controller.PostController;
import edu.yandex.project.factory.PostFactory;
import edu.yandex.project.repository.CommentRepository;
import edu.yandex.project.repository.PostRepository;
import edu.yandex.project.repository.TagRepository;
import edu.yandex.project.service.impl.CommentServiceImpl;
import edu.yandex.project.service.impl.ImageServiceImpl;
import edu.yandex.project.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import({
        CommentServiceImpl.class,
        ImageServiceImpl.class,
        PostFactory.class,
        PostServiceImpl.class
})
@WebMvcTest({CommentController.class, PostController.class})
public abstract class AbstractGlobalExceptionHandlerIT {

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected CommentRepository mockedCommentRepository;
    @MockitoBean
    protected PostRepository mockedPostRepository;
    @MockitoBean
    protected TagRepository tagRepository;
}
