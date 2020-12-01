package com.cist2931.petstore.application.employee;

import com.cist2931.petstore.application.PasswordHelper;
import com.cist2931.petstore.application.customer.Customer;
import com.cist2931.petstore.application.customer.CustomerSQL;
import com.cist2931.petstore.application.merchandise.Merchandise;
import com.cist2931.petstore.application.merchandise.MerchandiseSQL;
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
        } else return HttpStatus.UNAUTHORIZED_401;
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

    public int setOrderStatus(int orderNo, String status) {
        int responseCode;

        if (orderNo == -1) {
            responseCode = HttpStatus.BAD_REQUEST_400;
        } else {
            try {
                Optional<Order> orderOptional = OrderSQL.getOrderByOrderID(conn, orderNo);
                if (orderOptional.isPresent()) {
                    Order order = orderOptional.get();
                    order.setStatus(status);
                    order.update(conn);
                    responseCode = HttpStatus.OK_200;
                    logger.info("Updated order(" + orderNo + ") status to \"" + status + "\"");
                } else {
                    logger.error("Failed to update order(" + orderNo + ") status - Not found!");
                    responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
                }
            } catch (SQLException ex) {
                logger.error("Failed to update order(" + orderNo + ") status!", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        }

        return responseCode;
    }

    public class OrderInfo {
        public final int orderNo;
        public final String timestamp;
        public final String status;
        public final String address;
        public final double total;

        public OrderInfo(Order order) {
            orderNo = order.getOrderID();
            timestamp = order.getPrettyTimestamp();
            status = order.getStatus();

            String addr = "Unknown";
            Optional<Customer> customerOptional = CustomerSQL.getCustomerById(conn, order.getCustomerID());
            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                addr = customer.getStreet() + " " + customer.getCity() + ", " + customer.getState() + " " + customer.getZipcode();
            }
            address = addr;

            double orderTotal = 0;
            List<OrderMerchandise> items = order.getOrderMerchandiseContainer().getMerchandiseList();
            for (OrderMerchandise merch : items) {
                Optional<Merchandise> merchInfo = MerchandiseSQL.getMerchandiseById(conn, merch.getMerchandiseID());
                if (merchInfo.isPresent()) {
                    orderTotal += merch.getMerchandiseQuantity() * merchInfo.get().getPrice();
                }
            }
            total = orderTotal;
        }
    }

    public Pair<Integer, List<OrderInfo>> getOrders(String token) {
        int responseCode;
        List<OrderInfo> orderInfos = new ArrayList<>();

        Optional<Employee> employeeOptional = EmployeeSQL.getEmployeeByToken(conn, token);
        if(employeeOptional.isPresent()) {
            try {
                List<Order> orders = OrderSQL.getAllOrders(conn);
                for (Order order : orders) {
                    orderInfos.add(new OrderInfo(order));
                }
                responseCode = HttpStatus.OK_200;
            } catch (SQLException ex) {
                logger.error("Failed to get orders", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else {
            responseCode = HttpStatus.FORBIDDEN_403;
        }
        return Pair.of(responseCode, orderInfos);
    }
}
