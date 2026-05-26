package br.com.fiap.orbitcast.connection;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

@ApplicationScoped
public class DatabaseInitializer {

    @Inject
    DatabaseConnection databaseConnection;

    void onStart(@Observes StartupEvent event) {
        executeSql("db/schema.sql");
        executeSql("db/data.sql");
    }

    private void executeSql(String resourcePath) {
        String sql = loadScript(resourcePath);

        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            for (String command : sql.split(";")) {
                String sanitized = command.strip();
                if (!sanitized.isEmpty()) {
                    statement.execute(sanitized);
                }
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao executar script " + resourcePath, exception);
        }
    }

    private String loadScript(String resourcePath) {
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalStateException("Recurso nao encontrado: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .filter(line -> !line.strip().startsWith("--"))
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao ler recurso " + resourcePath, exception);
        }
    }
}
