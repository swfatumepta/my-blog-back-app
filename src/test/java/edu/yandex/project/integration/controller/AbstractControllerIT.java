package edu.yandex.project.integration.controller;

import edu.yandex.project.integration.AbstractPostgreSQLContainerIT;
import edu.yandex.project.integration.config.AppITConfig;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@SpringJUnitWebConfig(AppITConfig.class)
public class AbstractControllerIT extends AbstractPostgreSQLContainerIT {
}
