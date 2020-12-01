package com.cist2931.petstore.application.customer;

import com.cist2931.petstore.StringUtils;
import com.cist2931.petstore.application.AuthenticationService;
import com.cist2931.petstore.application.PasswordHelper;
import com.cist2931.petstore.application.order.Order;
import com.cist2931.petstore.application.order.OrderMerchandise;
import com.cist2931.petstore.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public final class CustomerController {

    private static final Logger logger = new Logger(CustomerController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CustomerService customerService;

    public CustomerController(Connection dbConnection) {
        customerService = new CustomerService(dbConnection);
    }

    public void doCreate(Context ctx) {
        String password = ctx.formParam("password");
        String firstName = ctx.formParam("firstName");
        String lastName = ctx.formParam("lastName");
        String street = ctx.formParam("street");
        String city = ctx.formParam("city");
        String state = ctx.formParam("state");
        String zipcodeRaw = ctx.formParam("zipcode");
        String phoneNumber = ctx.formParam("phoneNumber");
        String email = ctx.formParam("email");

        if (!StringUtils.hasValue(password, firstName, lastName, street, city, zipcodeRaw, phoneNumber, email)) {
            ctx.status(HttpStatus.BAD_REQUEST_400);
            return;
        }

        //noinspection ConstantConditions
        int zipcode = Integer.parseInt(zipcodeRaw);

        String hashedPassword = PasswordHelper.hashPassword(password);

        Customer customer = new Customer(-1, hashedPassword, firstName, lastName, street, city, state, zipcode, phoneNumber, email, null);

        int respCode = customerService.create(customer);

        ctx.status(respCode);
    }

    public void doLogin(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        Pair<Integer, String> loginResponse = customerService.login(email, password);
        ctx.status(loginResponse.getLeft());

        if (loginResponse.getLeft() == HttpStatus.OK_200) {
            AuthenticationService.storeCustomerToken(ctx, loginResponse.getRight());
        }
    }

    public void doLogout(Context ctx) {
        String token = AuthenticationService.getCustomerToken(ctx);

        int respCode = customerService.logout(token);

        ctx.status(respCode);

        if (respCode == HttpStatus.OK_200) {
            AuthenticationService.storeCustomerToken(ctx, "");
        }
    }

    public void doChangePassword(Context ctx) {
        String token = AuthenticationService.getCustomerToken(ctx);
        String newPassword = ctx.formParam("newPassword");

        int respCode = customerService.changePassword(token, newPassword);

        ctx.status(respCode);
    }

    public void getOrders(Context ctx) {
        String token = AuthenticationService.getCustomerToken(ctx);

        Pair<Integer, List<Order>> response = customerService.getOrders(token);

        if (response.getLeft() == HttpStatus.OK_200) {
            ctx.json(response.getRight());
        }

        ctx.status(response.getLeft());
    }

    public void doPlaceOrder(Context ctx) {
        String token = AuthenticationService.getCustomerToken(ctx);
        OrderMerchandise[] orderItems = ctx.bodyAsClass(OrderMerchandise[].class);

        Pair<Integer, Integer> response = customerService.placeOrder(token, orderItems);
        int responseCode = response.getLeft();
        int orderId = response.getRight();

        ctx.status(responseCode);
        if (orderId != -1) {
            String orderIdJson = "{\"orderID\": " + orderId + "}";
            ctx.result(orderIdJson);
        }
    }

    public void getCustomer(Context ctx) {
        String token = AuthenticationService.getCustomerToken(ctx);

        Pair<Integer, Customer> getResponse = customerService.getByToken(token);

        ctx.json(getResponse.getRight());
        ctx.status(getResponse.getLeft());
    }

    public void doUpdateInfo(Context ctx) {
        String token = AuthenticationService.getCustomerToken(ctx);
        String firstName = ctx.formParam("firstName");
        String lastName = ctx.formParam("lastName");
        String street = ctx.formParam("street");
        String city = ctx.formParam("city");
        String state = ctx.formParam("state");
        String zipcodeRaw = ctx.formParam("zipcode");
        String phoneNum = ctx.formParam("phoneNum");
        String password = ctx.formParam("password");

        if (zipcodeRaw == null) zipcodeRaw = "00000";
        int zipcode = Integer.parseInt(zipcodeRaw);

        Pair<Integer, String> updateResponse = customerService.updateInfo(token, firstName, lastName, street, city, state, zipcode, phoneNum, password);
        ctx.status(updateResponse.getLeft());

        if (updateResponse.getLeft() == HttpStatus.OK_200) {
            AuthenticationService.storeCustomerToken(ctx, updateResponse.getRight());
        }
    }

    public void doPlaceGuestOrder(Context ctx) throws JsonProcessingException {
        Map<String, List<String>> formParams = ctx.formParamMap();
        String items = ctx.formParam("items");
        OrderMerchandise[] actualItems = objectMapper.readValue(items, OrderMerchandise[].class);
        Guest guest = new Guest(ctx);

        Pair<Integer, Integer> response = customerService.placeGuestOrder(guest, actualItems);
        int responseCode = response.getLeft();
        int orderId = response.getRight();

        ctx.status(responseCode);
        if (orderId != -1) {
            String orderIdJson = "{\"orderID\": " + orderId + "}";
            ctx.result(orderIdJson);
        }
    }

    static class Guest {
        public final String firstName;
        public final String lastName;
        public final String street;
        public final String city;
        public final String state;
        public final String zip;
        public final String phoneNumber;
        public final String email;

        public Guest(Context ctx) {
            firstName = ctx.formParam("firstName");
            lastName = ctx.formParam("lastName");
            street = ctx.formParam("street");
            city = ctx.formParam("city");
            state = ctx.formParam("state");
            zip = ctx.formParam("zipcode");
            phoneNumber = ctx.formParam("phoneNumber");
            email = ctx.formParam("email");
        }
    }
}
