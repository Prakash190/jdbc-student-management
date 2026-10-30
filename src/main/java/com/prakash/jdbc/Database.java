package com.prakash.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates JDBC connections and ensures the required table exists. */
public final class Database {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/student_manager?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC";
    private static final String URL = environment("JDBC_URL", DEFAULT_URL);
    private static final String USER = environment("DB_USER", "root");
    private static final String PASSWORD = environment("DB_PASSWORD", "");

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeSchema() throws SQLException {
        String createTable = """
                CREATE TABLE IF NOT EXISTS students (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(120) NOT NULL UNIQUE,
                    course VARCHAR(100) NOT NULL,
                    age INT NOT NULL CHECK (age BETWEEN 1 AND 120)
                )
                """;

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(createTable);
        }
    }

    private static String environment(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
