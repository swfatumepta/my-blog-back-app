package edu.yandex.project.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.yandex.project.factory.PostFactory;
import edu.yandex.project.integration.AbstractDbIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public abstract class AbstractControllerIT extends AbstractDbIT {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PostFactory postFactory;
}
