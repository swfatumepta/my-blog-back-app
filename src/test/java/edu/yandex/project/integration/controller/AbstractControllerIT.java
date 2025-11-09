package edu.yandex.project.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.yandex.project.integration.AbstractDbIT;
import edu.yandex.project.integration.config.AppITConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(AppITConfig.class)
public class AbstractControllerIT extends AbstractDbIT {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
