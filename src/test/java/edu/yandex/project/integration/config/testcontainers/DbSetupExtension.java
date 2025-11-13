package edu.yandex.project.integration.config.testcontainers;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

// Интеерсное и, вероятно, самое оптимальное решение, которое позволяет пошарить контейнер между всеми тест-комплектами,
// которым он нужен (вмешиваемся в ЖЦ JUnit5 уонтекста)
public class DbSetupExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        ITPostgreSQLContainer.container.start();
        this.updateDatasourceProperties(ITPostgreSQLContainer.container);
    }

    private void updateDatasourceProperties(ITPostgreSQLContainer container) {
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.password", container.getPassword());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.driver-class-name", container.getDriverClassName());
    }
}
