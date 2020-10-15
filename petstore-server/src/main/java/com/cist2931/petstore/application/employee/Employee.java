package com.cist2931.petstore.application.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Employee {
    private int empID;
    private String username;
    @JsonIgnore private String password;
    private String firstName;
    private String lastName;
    @JsonIgnore private String authToken;

    public Employee(ResultSet rs) throws SQLException {
        // initialize from result set
        empID = rs.getInt("EmpID");
        username = rs.getString("Username");
        password = rs.getString("Password");
        firstName = rs.getString("FirstName");
        lastName = rs.getString("LastName");
        authToken = rs.getString("Token");
    }

    public Employee select(Connection dbConnection, int id) throws SQLException {
        return EmployeeSQL.getEmployeeById(dbConnection, id);
    }

    public boolean update(Connection dbConnection) throws SQLException {
        return EmployeeSQL.updateEmployee(dbConnection, this);
    }

    public boolean insert(Connection dbConnection) throws SQLException {
        return EmployeeSQL.insertEmployee(dbConnection, this);
    }

    public boolean delete(Connection dbConnection) throws SQLException {
        return EmployeeSQL.deleteEmployee(dbConnection, this.empID);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empID=" + empID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
