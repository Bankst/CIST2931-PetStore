package com.cist2931.petstore.application.customer;

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
    private String state;
    private int zipcode;
    private String phoneNumber;
    private String email;

    public Customer(int customerID, String password, String firstName, String lastName, String street, String city, String state, int zipcode, String phoneNumber, String email) {
        this.customerID = customerID;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Customer(ResultSet rs) throws SQLException {
        // initialize from result set
        this(
            rs.getInt("CustID"),
            rs.getString("Password"),
            rs.getString("FirstName"),
            rs.getString("LastName"),
            rs.getString("Street"),
            rs.getString("City"),
            rs.getString("State"),
            rs.getInt("Zipcode"),
            rs.getString("PhoneNum"),
            rs.getString("Email")
        );
    }

    public Customer() {
        this(-1, "", "", "", "", "", "", 0, "", "");
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

    public boolean update(Connection dbConnection) throws SQLException {
        return updateCustomer(dbConnection, this);
    }

    public boolean insert(Connection dbConnection) throws SQLException {
        return insertCustomer(dbConnection, this);
    }

    public boolean delete(Connection dbConnection) throws SQLException {
        return deleteCustomer(dbConnection, this.customerID);
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

    public void setstatemCity(String city) {
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

    public static Customer getCustomerById(Connection dbConnection, int id) throws SQLException {
        final String selectQuery = "SELECT * FROM Customer WHERE CustID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new Customer(resultSet);
        } else return null;
    }

    public static boolean insertCustomer(Connection dbConnection, Customer customer) throws SQLException {
        final String insertQuery = "INSERT INTO Customer(CustID, Password, FirstName, LastName, Street, City, State, Zipcode, PhoneNum, Email) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = dbConnection.prepareStatement(insertQuery);
        statement.setInt(1, customer.customerID);
        statement.setString(2, customer.password);
        statement.setString(3, customer.firstName);
        statement.setString(4, customer.lastName);
        statement.setString(5, customer.street);
        statement.setString(6, customer.city);
        statement.setString(7, customer.state);
        statement.setInt(8, customer.zipcode);
        statement.setString(9, customer.phoneNumber);
        statement.setString(10, customer.email);

        return statement.executeUpdate() == 1;
    }

    public static boolean updateCustomer(Connection dbConnection, Customer customer) throws SQLException {
        final String updateQuery = "UPDATE Customer set Password = ?, FirstName = ?, LastName = ?, Street = ?, City = ?, State = ?, Zipcode = ?, PhoneNum = ?, Email = ? WHERE CustID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(updateQuery);
        statement.setString(1, customer.password);
        statement.setString(2, customer.firstName);
        statement.setString(3, customer.lastName);
        statement.setString(4, customer.street);
        statement.setString(5, customer.city);
        statement.setString(6, customer.state);
        statement.setInt(7, customer.zipcode);
        statement.setString(8, customer.phoneNumber);
        statement.setString(9, customer.email);
        statement.setInt(10, customer.customerID);

        return statement.executeUpdate() == 1;
    }

    public static boolean deleteCustomer(Connection dbConnection, int id) throws SQLException {
        final String deleteQuery = "DELETE FROM Customer WHERE CustID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(deleteQuery);
        statement.setInt(1, id);

        return statement.executeUpdate() == 1;
    }
}