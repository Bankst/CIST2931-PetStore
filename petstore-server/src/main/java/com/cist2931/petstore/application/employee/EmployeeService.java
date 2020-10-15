package com.cist2931.petstore.application.employee;

import com.cist2931.petstore.application.PasswordHelper;
import com.cist2931.petstore.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class EmployeeService {

    private static final Logger logger = new Logger(EmployeeService.class);

    private final Connection conn;

    public EmployeeService(Connection conn) {
        this.conn = conn;
    }

    public Pair<Integer, String> login(String username, String password) {
        int responseCode;
        String token = "";

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            Optional<Employee> employeeOptional = EmployeeSQL.getEmployeeByUsername(conn, username);
            if (employeeOptional.isPresent()) {
                Employee employee = employeeOptional.get();
                if (PasswordHelper.verifyPassword(password, employee.getPassword())) {
                    token = UUID.randomUUID().toString();
                    employee.setAuthToken(token);
                    try {
                        employee.update(conn);
                        logger.info("Updated login token for employee(" + employee.getEmpID() +  ")");
                        responseCode = HttpStatus.OK_200; // OK - OK
                    } catch (SQLException ignored) {
                        logger.error("Failed to update employee(" + employee.getEmpID() +  ") row for login token!");
                        responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500; // Server Error - server error
                        token = "";
                    }
                } else responseCode = HttpStatus.UNAUTHORIZED_401; // Unauthorized - password incorrect
            } else responseCode = HttpStatus.FORBIDDEN_403; // Forbidden - account does not exist
        } else responseCode = HttpStatus.BAD_REQUEST_400; // Bad Request - request data malformed or invalid

        return Pair.of(responseCode, token);
    }

    public int logout(String token) {
        Optional<Employee> employeeOptional = EmployeeSQL.getEmployeeByToken(conn, token);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            employee.setAuthToken(null);

            try {
                employee.update(conn);
            } catch (SQLException ignored) {
                logger.error("Failed to update employee(" + employee.getEmpID() +  ") row for logout!");
                return HttpStatus.INTERNAL_SERVER_ERROR_500;
            }

            logger.info("Logged out employee(" + employee.getEmpID() + ")");
            return HttpStatus.OK_200;
        } else return HttpStatus.FORBIDDEN_403;
    }

    public int changePassword(String token, String newPassword) {
        Optional<Employee> employeeOptional = EmployeeSQL.getEmployeeByToken(conn, token);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();

            if (PasswordHelper.verifyPassword(newPassword, employee.getPassword())) return 304;

            employee.setPassword(PasswordHelper.hashPassword(newPassword));
            try {
                employee.update(conn);
                logger.info("Updated password for employee(" + employee.getEmpID() +  ")");
                return HttpStatus.OK_200;
            } catch (SQLException ignored) {
                logger.error("Failed to update employee(" + employee.getEmpID() +  ") row for new password!");
                return HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else return HttpStatus.FORBIDDEN_403;
    }

    public Pair<Integer, Employee> getByToken(String token) {
        int responseCode;
        Employee employee = null;

        Optional<Employee> employeeOptional =  EmployeeSQL.getEmployeeByToken(conn, token);
        if (employeeOptional.isPresent()) {
            employee = employeeOptional.get();
            responseCode = HttpStatus.OK_200;
            logger.info("Handled info request for employee(" + employee.getEmpID() + ")");
        } else {
            responseCode = HttpStatus.FORBIDDEN_403;
        }

        return Pair.of(responseCode, employee);
    }
}
