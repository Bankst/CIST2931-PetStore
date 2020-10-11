package com.cist2931.petstore.application.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer {

    private int customerID;
    @JsonIgnore private String password;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String state;
    private int zipcode;
    private String phoneNumber;
    private String email;
    @JsonIgnore private String authToken;

    public Customer(int customerID, String password, String firstName, String lastName, String street, String city, String state, int zipcode, String phoneNumber, String email, String authToken) {
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
        this.authToken = authToken;
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
            rs.getString("Email"),
            rs.getString("Token")
        );
    }

    public Customer() {
        this(-1, "", "", "", "", "", "", 0, "", "", "");
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
                ", state='" + state + '\'' +
                ", zipcode=" + zipcode +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }

    public boolean update(Connection conn) throws SQLException {
        return CustomerSQL.updateCustomer(conn, this);
    }

    public boolean insert(Connection conn) throws SQLException {
        return CustomerSQL.insertCustomer(conn, this);
    }

    public boolean delete(Connection conn) throws SQLException {
        return CustomerSQL.deleteCustomer(conn, this.customerID);
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    protected String getHashedPassword() {
        return password;
    }

    public void setHashedPassword(String password) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}