package com.cist2931.petstore;

import com.cist2931.petstore.database.DatabaseManager;
import com.cist2931.petstore.logging.LogLevel;
import com.cist2931.petstore.logging.Logger;
import com.cist2931.petstore.objects.Merchandise;

import java.sql.SQLException;

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

        Merchandise testMerch;
        try {
            testMerch = Merchandise.getMerchandiseById(dbManager.getDbConnection(), 500);
            if (testMerch != null) {
                logger.info("Got testMerch!");
                logger.debug(testMerch.toString());
            } else {
                testMerch = new Merchandise(500, "testMerch", 500.50, "Test", "Test Merchandise", 100);

                System.out.println(testMerch.insert(dbManager.getDbConnection()) ? "Insert Successful" : "Insert Failed");
            }

            testMerch.setQuantity(200);
            System.out.println(testMerch.update(dbManager.getDbConnection()) ? "Update Successful" : "Update Failed");

            System.out.println(testMerch.delete(dbManager.getDbConnection()) ? "Delete Successful" : "Delete Failed");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
