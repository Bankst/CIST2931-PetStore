package com.cist2931.petstore.database;

import com.cist2931.petstore.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final Logger logger = new Logger(DatabaseManager.class);

    private Connection dbConnection;

    public DatabaseManager() {}

    public boolean loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            logger.error("Failed to load JDBC driver!", e);
            return false;
        }
        logger.info("Loaded JDBC driver successfully!");
        return true;
    }

    public boolean initConnection(String sqlHost, String sqlUsername, String sqlPassword, String database) {
        try {
            String connectionString = "jdbc:mysql://" + sqlHost + "/" + database + "?user=" + sqlUsername + "&password=" + sqlPassword;
            dbConnection = DriverManager.getConnection(connectionString);
        } catch (SQLException e) {
            logger.error("Failed to connect to database!", e);
            logger.error("SQLError: " + e.getSQLState());
            logger.error("VendorError: " + e.getErrorCode());
            return false;
        }
        logger.info("Connected to SQL database successfully!");
        return true;
    }

}
