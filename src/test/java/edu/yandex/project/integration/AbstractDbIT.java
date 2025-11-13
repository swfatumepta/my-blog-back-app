package edu.yandex.project.integration;

import edu.yandex.project.integration.config.testcontainers.ext.DbSetupExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DbSetupExtension.class)
public abstract class AbstractDbIT {
}
