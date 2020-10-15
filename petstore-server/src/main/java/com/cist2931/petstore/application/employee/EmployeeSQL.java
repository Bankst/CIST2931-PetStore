package com.cist2931.petstore.application.employee;

import java.sql.*;
import java.util.Optional;

public final class EmployeeSQL {

    public static Employee getEmployeeById(Connection conn, int id) throws SQLException {
        final String selectQuery = "SELECT * FROM Employee WHERE EmpID = ?";
        PreparedStatement statement = conn.prepareStatement(selectQuery);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new Employee(resultSet);
        } else return null;
    }

    public static Optional<Employee> getEmployeeByUsername(Connection conn, String username) {
        final String selectQuery = "SELECT * FROM Employee WHERE Username = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(new Employee(resultSet)) : Optional.empty();
        } catch (SQLException ignored) {
            // TODO: Log errors
            return Optional.empty();
        }
    }

    public static Optional<Employee> getEmployeeByToken(Connection conn, String token) {
        final String selectQuery = "SELECT * FROM Employee WHERE Token = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            statement.setString(1, token);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(new Employee(resultSet)) : Optional.empty();
        } catch (SQLException ignored) {
            // TODO: Log errors
            return Optional.empty();
        }
    }

    public static boolean insertEmployee(Connection conn, Employee employee) throws SQLException {
        final String insertQuery = "INSERT INTO Customer(Username, Password, FirstName, LastName) values (?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, employee.getUsername());
        statement.setString(2, employee.getPassword());
        statement.setString(3, employee.getFirstName());
        statement.setString(4, employee.getLastName());

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            return false; // TODO: LOG
        } else {
            try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newKey = (int) generatedKeys.getLong(1);
                    employee.setEmpID(newKey);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public static boolean updateEmployee(Connection conn, Employee employee) throws SQLException {
        final String updateQuery = "UPDATE Employee set Username = ?, Password = ?, FirstName = ?, LastName = ?, Token = ? WHERE EmpID = ?";
        PreparedStatement statement = conn.prepareStatement(updateQuery);
        statement.setString(1, employee.getUsername());
        statement.setString(2, employee.getPassword());
        statement.setString(3, employee.getFirstName());
        statement.setString(4, employee.getLastName());
        statement.setString(5, employee.getAuthToken());
        statement.setInt(6, employee.getEmpID());

        return statement.executeUpdate() == 1;
    }

    public static boolean deleteEmployee(Connection conn, int id) throws SQLException {
        final String deleteQuery = "DELETE FROM Employee WHERE EmpID = ?";
        PreparedStatement statement = conn.prepareStatement(deleteQuery);
        statement.setInt(1, id);

        return statement.executeUpdate() == 1;
    }

}
