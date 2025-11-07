package edu.yandex.project.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.yandex.project.config.AppConfig;
import edu.yandex.project.controller.dto.post.PostPageDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@SpringJUnitConfig(classes = {AppConfig.class})
public class PostControllerIT {
    private final static String POSTS_ROOT = "/api/posts";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Test
    void getPosts_shouldThrow___() {
        // given
        var requestParams = Map.of(
                PostPageRequestParameters.Fields.search, "",
                PostPageRequestParameters.Fields.pageNumber, "0",
                PostPageRequestParameters.Fields.pageSize, "5"
        );
        // when
        var response = mockMvc.perform(get(POSTS_ROOT).params(MultiValueMap.fromSingleValue(requestParams)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.hasNext").isBoolean())
                .andExpect(jsonPath("$.hasPrev").isBoolean())
                .andExpect(jsonPath("$.lastPage").hasJsonPath())
                .andReturn().getResponse().getContentAsString();

        var parsedResponse = objectMapper.readValue(response, PostPageDto.class);
        System.out.println(parsedResponse);
    }
}
