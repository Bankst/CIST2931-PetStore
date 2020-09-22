package com.cist2931.petstore.application.customer;

import com.cist2931.petstore.application.order.OrderMerchandiseSQL;

import java.sql.*;
import java.util.Optional;

public final class CustomerSQL {

    public static Customer getCustomerById(Connection conn, int id) throws SQLException {
        final String selectQuery = "SELECT * FROM Customer WHERE CustID = ?";
        PreparedStatement statement = conn.prepareStatement(selectQuery);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new Customer(resultSet);
        } else return null;
    }

    public static Optional<Customer> getCustomerByEmail(Connection conn, String email) {
        final String selectQuery = "SELECT * FROM Customer WHERE Email = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(new Customer(resultSet)) : Optional.empty();
        } catch (SQLException ignored) {
            // TODO: Log errors
            return Optional.empty();
        }
    }

    public static Optional<Customer> getCustomerByToken(Connection conn, String token) {
        final String selectQuery = "SELECT * FROM Customer WHERE Token = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            statement.setString(1, token);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(new Customer(resultSet)) : Optional.empty();
        } catch (SQLException ignored) {
            // TODO: Log errors
            return Optional.empty();
        }
    }

    public static boolean insertCustomer(Connection conn, Customer customer) throws SQLException {
        final String insertQuery = "INSERT INTO Customer(Password, FirstName, LastName, Street, City, State, Zipcode, PhoneNum, Email) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, customer.getHashedPassword());
        statement.setString(2, customer.getFirstName());
        statement.setString(3, customer.getLastName());
        statement.setString(4, customer.getStreet());
        statement.setString(5, customer.getCity());
        statement.setString(6, customer.getState());
        statement.setInt(7, customer.getZipcode());
        statement.setString(8, customer.getPhoneNumber());
        statement.setString(9, customer.getEmail());

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            return false; // TODO: LOG
        } else {
            try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newKey = (int) generatedKeys.getLong(1);
                    customer.setCustomerID(newKey);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public static boolean updateCustomer(Connection conn, Customer customer) throws SQLException {
        final String updateQuery = "UPDATE Customer set Password = ?, FirstName = ?, LastName = ?, Street = ?, City = ?, State = ?, Zipcode = ?, PhoneNum = ?, Email = ?, Token = ? WHERE CustID = ?";
        PreparedStatement statement = conn.prepareStatement(updateQuery);
        statement.setString(1, customer.getHashedPassword());
        statement.setString(2, customer.getFirstName());
        statement.setString(3, customer.getLastName());
        statement.setString(4, customer.getStreet());
        statement.setString(5, customer.getCity());
        statement.setString(6, customer.getState());
        statement.setInt(7, customer.getZipcode());
        statement.setString(8, customer.getPhoneNumber());
        statement.setString(9, customer.getEmail());
        statement.setString(10, customer.getAuthToken());
        statement.setInt(11, customer.getCustomerID());

        return statement.executeUpdate() == 1;
    }

    public static boolean deleteCustomer(Connection conn, int id) throws SQLException {
        final String deleteQuery = "DELETE FROM Customer WHERE CustID = ?";
        PreparedStatement statement = conn.prepareStatement(deleteQuery);
        statement.setInt(1, id);

        return statement.executeUpdate() == 1;
    }
}
