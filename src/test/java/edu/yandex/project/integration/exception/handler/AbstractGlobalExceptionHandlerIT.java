package edu.yandex.project.integration.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.yandex.project.integration.config.GlobalExceptionHandlerITConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("exception")
@SpringJUnitWebConfig(GlobalExceptionHandlerITConfig.class)
public abstract class AbstractGlobalExceptionHandlerIT {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
