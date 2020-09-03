package com.cist2931.petstore;

import com.cist2931.petstore.database.DatabaseManager;
import com.cist2931.petstore.logging.LogLevel;
import com.cist2931.petstore.logging.Logger;
import com.cist2931.petstore.objects.Customer;

import java.sql.ResultSet;
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

        Customer bankst = null;
        try {
            bankst = Customer.GetCustomerByID(dbManager.getDbConnection(), 0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (bankst != null) {
            logger.info("Got bankst!");
            logger.debug(bankst.toString());
        }
//        ResultSet testTableData = dbManager.runQuery("INSERT INTO Customer (Password, FirstName, LastName, Street, City, State, Zipcode, Email) VALUES ('testpassword', 'John', 'Doe', '123 Main St', 'Townsville', 123456, 'Georgia')");
    }

}
