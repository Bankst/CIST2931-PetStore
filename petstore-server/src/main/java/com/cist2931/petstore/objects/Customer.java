package com.cist2931.petstore.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer {

    private int customerID;
    private String password;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private int zipcode;
    private String phoneNumber;
    private String email;

    public Customer() {
        //default constructor
        customerID = 0;
        password = "";
        firstName = "";
        lastName = "";
        street = "";
        city = "";
        zipcode = 0;
        phoneNumber = "";
        email = "";
    }

    public Customer(ResultSet rs) throws SQLException {
        // initialize from result set
        customerID = rs.getInt("CustID");
        password = rs.getString("Password");
        firstName = rs.getString("FirstName");
        lastName = rs.getString("LastName");
        street = rs.getString("Street");
        city = rs.getString("city");
        zipcode = rs.getInt("Zipcode");
        phoneNumber = rs.getString("PhoneNum");
        email = rs.getString("Email");
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

    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + customerID +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipcode=" + zipcode +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}