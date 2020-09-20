package com.cist2931.petstore.application.customer;

import com.cist2931.petstore.application.PasswordHelper;
import com.cist2931.petstore.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class CustomerService {

    private static final Logger logger = new Logger(CustomerService.class);

    private final Connection conn;

    public CustomerService(Connection conn) {
        this.conn = conn;
    }

    public Pair<Integer, String> login(String email, String password) {
        int responseCode;
        String token = "";
        Customer customer = null;

        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            Optional<Customer> customerOptional = CustomerSQL.getCustomerByEmail(conn, email);
            if (customerOptional.isPresent()) {
                customer = customerOptional.get();
                if (PasswordHelper.verifyPassword(password, customer.getHashedPassword())) {
                    token = UUID.randomUUID().toString();
                    customer.setAuthToken(token);
                    try {
                        customer.update(conn);
                        logger.info("Updated login token for customer(" + customer.getCustomerID() +  ")");
                        responseCode = 200; // OK - OK
                    } catch (SQLException ignored) {
                        logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for login token!");
                        responseCode = 500; // Server Error - server error
                        token = "";
                    }
                } else responseCode = 401; // Unauthorized - password incorrect
            } else responseCode = 403; // Forbidden - account does not exist
        } else responseCode = 400; // Bad Request - request data malformed or invalid

        return Pair.of(responseCode, token);
    }

    public int logout(String token) {
        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setAuthToken(null);

            try {
                customer.update(conn);
            } catch (SQLException ignored) {
                logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for logout!");
                return 500;
            }

            return 200;
        } else return 403;
    }

    public int changePasssword(String token, String newPassword) {
        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            if (PasswordHelper.verifyPassword(newPassword, customer.getHashedPassword())) return 304;

            customer.setHashedPassword(PasswordHelper.hashPassword(newPassword));
            try {
                customer.update(conn);
                logger.info("Updated password for customer(" + customer.getCustomerID() +  ")");
                return 200;
            } catch (SQLException ignored) {
                logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for new password!");
                return 500;
            }
        } else return 403;
    }

    public Pair<Integer, Customer> getByToken(String token) {
        int responseCode;
        Customer customer = null;

        Optional<Customer> customerOptional =  CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            customer = customerOptional.get();
            responseCode = 200;
        } else {
            responseCode = 403;
        }
        return Pair.of(responseCode, customer);
    }
}
