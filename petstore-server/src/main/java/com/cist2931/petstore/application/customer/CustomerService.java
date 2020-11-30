package com.cist2931.petstore.application.customer;

import com.cist2931.petstore.StringUtils;
import com.cist2931.petstore.application.PasswordHelper;
import com.cist2931.petstore.application.order.Order;
import com.cist2931.petstore.application.order.OrderMerchandise;
import com.cist2931.petstore.application.order.OrderSQL;
import com.cist2931.petstore.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class CustomerService {

    private static final Logger logger = new Logger(CustomerService.class);

    private final Connection conn;

    public CustomerService(Connection conn) {
        this.conn = conn;
    }

    /**
     * Logs in a customer and returns an authentication token for their session.
     * 
     * @param email Email address of the customer logging in.
     * @param password Password of the customer logging in.
     * @return Returns a {@link Pair} containing an HTTP response code ({@link Integer}) on the left,
     * and when successful, the order ID ({@link Integer}) of the placed order.<br>
     *     Response codes:<br>
     *         200 = Successfully logged in<br>
     *         400 = Missing request data<br>
     *         401 = Password incorrect for given email address<br>
     *         403 = No customer found for given email address<br>
     *         500 = Internal server error
     */
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
                    } catch (SQLException ex) {
                        logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for login token!", ex);
                        responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500; // Server Error - server error
                        token = "";
                    }
                } else responseCode = HttpStatus.UNAUTHORIZED_401; // Unauthorized - password incorrect
            } else responseCode = HttpStatus.FORBIDDEN_403; // Forbidden - account does not exist
        } else responseCode = HttpStatus.BAD_REQUEST_400; // Bad Request - request data malformed or invalid

        return Pair.of(responseCode, token);
    }

    /**
     * Logs out a customer.
     *
     * @param token Token of the customer to log out.
     * @return Returns an HTTP response code.<br>
     *     Response codes:<br>
     *         200 = Successfully changed customer password<br>
     *         403 = No customer found for given token<br>
     *         500 = Internal server error
     */
    public int logout(String token) {
        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setAuthToken(null);

            try {
                customer.update(conn);
                logger.info("Logged out customer(" + customer.getCustomerID() + ")");
                return HttpStatus.OK_200;
            } catch (SQLException ex) {
                logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for logout!", ex);
                return HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else return HttpStatus.FORBIDDEN_403;
    }

    /**
     * Changes a customers password by their token and new password
     *
     * @param token Token of the customer to change the password for
     * @param newPassword New password
     * @return Returns an HTTP response code.<br>
     *     Response codes:<br>
     *         200 = Successfully changed customer password<br>
     *         304 = Did not change password - password was the same<br>
     *         403 = No customer found for given token<br>
     *         500 = Internal server error
     */
    public int changePassword(String token, String newPassword) {
        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            if (PasswordHelper.verifyPassword(newPassword, customer.getHashedPassword())) return HttpStatus.NOT_MODIFIED_304;

            customer.setHashedPassword(PasswordHelper.hashPassword(newPassword));
            try {
                customer.update(conn);
                logger.info("Updated password for customer(" + customer.getCustomerID() +  ")");
                return HttpStatus.OK_200;
            } catch (SQLException ex) {
                logger.error("Failed to update customer(" + customer.getCustomerID() +  ") row for new password!", ex);
                return HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else return HttpStatus.FORBIDDEN_403;
    }

    /**
     * Gets a {@link Customer} by their token.
     *
     * @param token Token of the customer to retrieve information for
     * @return Returns a {@link Pair} containing an HTTP response code ({@link Integer}) on the left,
     * and when successful, the {@link Customer} object containing the customer information.<br>
     *     Response codes:<br>
     *         200 = Successfully retrieved customer information<br>
     *         403 = No customer found for given token<br>
     *         500 = Internal server error
     */
    public Pair<Integer, Customer> getByToken(String token) {
        int responseCode;
        Customer customer = null;

        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            customer = customerOptional.get();
            responseCode = HttpStatus.OK_200;
            logger.info("Handled info request for customer(" + customer.getCustomerID() + ")");
        } else {
            responseCode = HttpStatus.FORBIDDEN_403;
        }

        return Pair.of(responseCode, customer);
    }

    /**
     * Places an order for a customer by their token and an array of {@link OrderMerchandise}
     *
     * @param token Token of the customer placing the order
     * @param orderItems Items to add to the order
     * @return Returns a {@link Pair} containing an HTTP response code ({@link Integer}) on the left,
     * and when successful, the order ID ({@link Integer}) of the placed order.<br>
     *     Response codes:<br>
     *         200 = Successfully placed order<br>
     *         403 = No customer found for given token<br>
     *         500 = Internal server error
     */
    public Pair<Integer, Integer> placeOrder(String token, OrderMerchandise[] orderItems) {
        int responseCode;
        int orderNo = -1;

        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            Order newOrder = new Order();
            newOrder.setStatus("Placed");
            newOrder.getOrderMerchandiseContainer().addItems(orderItems);
            newOrder.setCustomerID(customer.getCustomerID());

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

    /**
     *  Gets a customers {@link Order}s by their token.
     *
     * @param token Token of the customer requesting their orders
     * @return Returns a {@link Pair} containing an HTTP response code ({@link Integer}) on the left,
     * and when successful, a {@link List} of the customer orders ({@link Order}) on the right.<br>
     *     Response codes:<br>
     *     200 = Successfully got orders<br>
     *     403 = No customer found for given token<br>
     *     500 = Internal server error
     */
    public Pair<Integer, List<Order>> getOrders(String token) {
        int responseCode;
        List<Order> orders = new ArrayList<>();

        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            try {
                orders = OrderSQL.getOrdersByCustomerID(conn, customer.getCustomerID());
                responseCode = HttpStatus.OK_200;
            } catch (SQLException ex) {
                logger.error("Failed to get orders for customer(" + customer.getCustomerID() + ")", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else {
            responseCode = HttpStatus.FORBIDDEN_403;
        }

        return Pair.of(responseCode, orders);
    }

    /**
     * Creates a new customer with the given information.
     *
     * @param customer Customer to create
     * @return Returns an HTTP response code to indicate the result.<br>
     *         200 = Created customer successfully<br>
     *         409 = Customer with given email already exists<br>
     *         500 = Internal server error
     */
    public int create(Customer customer) {
        int responseCode;

        Optional<Customer> customerOptional = CustomerSQL.getCustomerByEmail(conn, customer.getEmail());
        if (customerOptional.isPresent()) {
            responseCode = HttpStatus.CONFLICT_409; // Customer already exists by that email address
        } else {
            try {
                if (customer.insert(conn)) {
                    responseCode = HttpStatus.OK_200;
                } else {
                    logger.error("Unknown failure to insert new customer(" + customer.getEmail() + ")");
                    responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
                }
            } catch (SQLException ex) {
                logger.error("Failed to insert new customer(" + customer.getEmail() + ")", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        }

        return responseCode;
    }

    public Pair<Integer, String> updateInfo(String token, String firstName, String lastName, String street, String city, String state, int zipcode, String phoneNum, String password) {
        int responseCode;
        Optional<Customer> customerOptional = CustomerSQL.getCustomerByToken(conn, token);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            boolean shouldChangePassword = StringUtils.hasValue(password) && PasswordHelper.verifyPassword(password, customer.getHashedPassword());
            if(shouldChangePassword) {
                customer.setHashedPassword(PasswordHelper.hashPassword(password));
                token = UUID.randomUUID().toString();
                customer.setAuthToken(token);
                logger.info("Changed password for customer(" + customer.getCustomerID() + ") - pending DB update");
            }

            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setStreet(street);
            customer.setCity(city);
            customer.setState(state);
            customer.setZipcode(zipcode);
            customer.setPhoneNumber(phoneNum);

            try {
                customer.update(conn);
                logger.info("Updated info for customer(" + customer.getCustomerID() + ")");

                responseCode = HttpStatus.OK_200;
            } catch (SQLException ex) {
                logger.error("Failed to update customer(" + customer.getCustomerID() + ") row for info!", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }

        } else responseCode = HttpStatus.FORBIDDEN_403;
        return Pair.of(responseCode, token);
    }
}
