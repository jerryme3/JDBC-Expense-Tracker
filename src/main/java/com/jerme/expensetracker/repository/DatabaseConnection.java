package com.jerme.expensetracker.repository;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Dotenv DOTENV = Dotenv.load();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DOTENV.get("DB_URL"),
                DOTENV.get("DB_USER"),
                DOTENV.get("DB_PASS")
        );
    }


}
