package com.cist2931.petstore;

import com.cist2931.petstore.application.RestServer;
import com.cist2931.petstore.database.DatabaseManager;
import com.cist2931.petstore.logging.LogLevel;
import com.cist2931.petstore.logging.Logger;

public class Main {
    private static final Logger logger = new Logger(Main.class);

    public static void main(String[] args) {
        Logger.setLogLevel(LogLevel.TRACE);
        logger.info("Hello CIST2931! Booting PetStore Backend...");

        DatabaseManager dbManager = new DatabaseManager();
        if (!dbManager.loadDriver()) {
            logger.error("No database driver, exiting program.");
            return;
        } else {
            if (!dbManager.initConnection("68.117.215.47:8291", "petStoreWeb", "902251430D2CDB269FB6CF11F66704E3", "petstoreOne")) {
                logger.error("No database connection, exiting program.");
                return;
            }
        }
        logger.info("SQL database init complete.");

        logger.info("Starting REST API");
        // this MUST be the last call. this takes over the thread and keeps the program running
        new RestServer(8080, dbManager.getConnection());
    }
}
