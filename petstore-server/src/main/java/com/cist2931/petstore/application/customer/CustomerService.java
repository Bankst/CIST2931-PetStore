package com.cist2931.petstore.application.customer;

import com.cist2931.petstore.application.PasswordHelper;
import com.cist2931.petstore.application.order.Order;
import com.cist2931.petstore.application.order.OrderMerchandise;
import com.cist2931.petstore.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

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

        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            Optional<Customer> customerOptional = CustomerSQL.getCustomerByEmail(conn, email);
            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                if (PasswordHelper.verifyPassword(password, customer.getHashedPassword())) {
                    token = UUID.randomUUID().toString();
                    customer.setAuthToken(token);
                    try {
                        customer.update(conn);
                        logger.info("Updated login token for customer(" + customer.getCustomerID() +  ")");
                        responseCode = HttpStatus.OK_200; // OK - OK
                    } catch (SQLException ignored) {
                        logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for login token!");
                        responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500; // Server Error - server error
                        token = "";
                    }
                } else responseCode = HttpStatus.UNAUTHORIZED_401; // Unauthorized - password incorrect
            } else responseCode = HttpStatus.FORBIDDEN_403; // Forbidden - account does not exist
        } else responseCode = HttpStatus.BAD_REQUEST_400; // Bad Request - request data malformed or invalid

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
                return HttpStatus.INTERNAL_SERVER_ERROR_500;
            }

            logger.info("Logged out customer(" + customer.getCustomerID() + ")");
            return HttpStatus.OK_200;
        } else return HttpStatus.FORBIDDEN_403;
    }

    public int changePassword(String token, String newPassword) {
        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            if (PasswordHelper.verifyPassword(newPassword, customer.getHashedPassword())) return 304;

            customer.setHashedPassword(PasswordHelper.hashPassword(newPassword));
            try {
                customer.update(conn);
                logger.info("Updated password for customer(" + customer.getCustomerID() +  ")");
                return HttpStatus.OK_200;
            } catch (SQLException ignored) {
                logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for new password!");
                return HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else return HttpStatus.FORBIDDEN_403;
    }

    public Pair<Integer, Customer> getByToken(String token) {
        int responseCode;
        Customer customer = null;

        Optional<Customer> customerOptional =  CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            customer = customerOptional.get();
            responseCode = HttpStatus.OK_200;
            logger.info("Handled info request for customer(" + customer.getCustomerID() + ")");
        } else {
            responseCode = HttpStatus.FORBIDDEN_403;
        }

        return Pair.of(responseCode, customer);
    }

    public Pair<Integer, Integer> placeOrder(String token, OrderMerchandise[] orderItems) {
        int orderNo = -1;
        int responseCode;

        Optional<Customer> customerOptional =  CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            Order newOrder = new Order();
            newOrder.setStatus("Placed");
            newOrder.getOrderMerchandiseContainer().addItems(orderItems);

            try {
                newOrder.insert(conn);
                orderNo = newOrder.getOrderID();
                responseCode = HttpStatus.OK_200;
            } catch (SQLException ex) {
                logger.error("Failed to insert order for customer(" + customer.getCustomerID() + ")", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else {
            responseCode = HttpStatus.FORBIDDEN_403;
        }

        return Pair.of(responseCode, orderNo);
    }
}
