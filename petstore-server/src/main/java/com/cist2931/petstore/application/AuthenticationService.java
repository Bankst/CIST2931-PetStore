package com.cist2931.petstore.application;

import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {
    private final Connection conn;

    public AuthenticationService(Connection conn) {
        this.conn = conn;
    }

    /**
     * Checks for login token of a customer
     * @param token token of customer to authorize
     * @return returns customerID if OK, -1 if failed
     */
    public int authorizeCustomer(String token) {
        final String selectQuery = "SELECT CustID, Token FROM Customer WHERE Token = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            statement.setString(1, token);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("CustID");
            }
        } catch (SQLException ignored) {
            // TODO: Log
        }
        return -1;
    }

    /**
     * Checks for login token of a customer
     * @param token token of customer to authorize
     * @return returns customerID if OK, -1 if failed
     */
    public int authorizeEmployee(String token) {
        final String selectQuery = "SELECT EmpID, Token FROM Employee WHERE Token = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            statement.setString(1, token);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("EmpID");
            }
        } catch (SQLException ignored) {
            // TODO: Log
        }
        return -1;
    }

    public static String getCustomerToken(Context ctx) {
        return ctx.cookieStore("customerAuthToken");
    }

    public static void storeCustomerToken(Context ctx, String token) {
        ctx.cookieStore("customerAuthToken", token);
    }

    public static String getEmployeeToken(Context ctx) {
        return ctx.cookieStore("employeeAuthToken");
    }

    public static void storeEmployeeToken(Context ctx, String token) {
        ctx.cookieStore("employeeAuthToken", token);
    }
}
