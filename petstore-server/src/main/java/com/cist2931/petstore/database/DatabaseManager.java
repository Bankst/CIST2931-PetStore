package com.cist2931.petstore.database;

import com.cist2931.petstore.logging.Logger;

import java.sql.*;

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

    public void runQuery(String query) {
        try {
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = null;
            if (query.startsWith("SELECT")) {
                if (statement.execute(query)) {
                    resultSet = statement.getResultSet();
                }
            }
            if (resultSet != null) {
                dumpAllSelect(resultSet);
            }

        } catch (SQLException e) {
            logger.error("Failed to run query!", e);
            logger.error("SQLError: " + e.getSQLState());
            logger.error("VendorError: " + e.getErrorCode());
        }
    }


    private void dumpAllSelect(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        for (int i = 1; i <= columnsNumber; i++) {
            System.out.print(rsmd.getColumnName(i) + "\t");
        }
        System.out.println();

        while (resultSet.next()) {

            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println();
        }
    }
}
