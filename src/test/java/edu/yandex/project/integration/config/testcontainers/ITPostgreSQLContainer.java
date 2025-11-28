package edu.yandex.project.integration.config.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class ITPostgreSQLContainer extends PostgreSQLContainer<ITPostgreSQLContainer> {
    private static final String IMAGE_VERSION = "postgres:16-alpine";
    private static final String DATABASE_NAME = "ya_blog";
    private static final String USERNAME = "test-user";
    private static final String PASSWORD = "test-pass";

    public static ITPostgreSQLContainer container = new ITPostgreSQLContainer()
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD);

    public ITPostgreSQLContainer() {
        super(IMAGE_VERSION);
    }
}
