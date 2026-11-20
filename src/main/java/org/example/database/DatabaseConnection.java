package org.example.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5433/eventify_db";
    private static final String DEFAULT_USER = "postgres";

    public String getUrl() {
        return URL;
    }

    public String getUser() {
        String user = System.getenv("EVENTIFY_DB_USER");
        return user == null || user.isBlank() ? DEFAULT_USER : user;
    }

    public String getPassword() throws SQLException {
        String password = System.getenv("EVENTIFY_DB_PASSWORD");
        if (password != null && !password.isBlank()) {
            return password;
        }

        Path configPath = Path.of("database.properties");
        if (Files.isRegularFile(configPath)) {
            Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new SQLException("Impossible de lire database.properties.", exception);
            }
            password = properties.getProperty("db.password");
            if (password != null && !password.isBlank()) {
                return password;
            }
        }

        throw new SQLException(
                "Set EVENTIFY_DB_PASSWORD or db.password in database.properties."
        );
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException(
                    "Pilote PostgreSQL absent. Ajoutez le fichier JAR JDBC PostgreSQL au projet IntelliJ.",
                    exception
            );
        }
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

}
