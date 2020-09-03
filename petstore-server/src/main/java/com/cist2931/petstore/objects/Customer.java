package com.cist2931.petstore.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer {

    public int customerID;
    public String firstName;
    public String lastName;
    public String street;
    public String city;
    public int zipcode;
    public String phoneNumber;
    public String email;

    public Customer(ResultSet rs) throws SQLException {
        // initialize from result set
        customerID = rs.getInt("CustID");
        firstName = rs.getString("FirstName");
        lastName = rs.getString("LastName");
        street = rs.getString("Street");
        city = rs.getString("city");
        zipcode = rs.getInt("Zipcode");
        phoneNumber = rs.getString("PhoneNum");
        email = rs.getString("Email");
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + customerID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipcode=" + zipcode +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }



    public static Customer GetCustomerByID(Connection dbConnection, int id) throws SQLException {
        final String selectQuery = "SELECT * FROM Customer WHERE CustID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new Customer(resultSet);
        } else return null;
    }
}