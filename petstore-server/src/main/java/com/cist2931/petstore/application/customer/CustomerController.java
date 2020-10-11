package com.cist2931.petstore.application.customer;

import com.cist2931.petstore.application.order.OrderMerchandise;
import com.cist2931.petstore.logging.Logger;
import io.javalin.http.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;

public final class CustomerController {

    private static final Logger logger = new Logger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(Connection dbConnection) {
        customerService = new CustomerService(dbConnection);
    }

    public void doCreate(Context ctx) {
        String email = ctx.queryParam("email");
        String password = ctx.queryParam("password");
    }

    public void doLogin(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        Pair<Integer, String> loginResponse = customerService.login(email, password);
        ctx.status(loginResponse.getLeft());

        if (loginResponse.getLeft() == HttpStatus.OK_200) {
            ctx.cookieStore("authToken", loginResponse.getRight());
        }
    }

    public void doLogout(Context ctx) {
        String token = ctx.cookieStore("authToken");

        int respCode = customerService.logout(token);

        ctx.status(respCode);

        if (respCode == HttpStatus.OK_200) {
            ctx.cookieStore("authToken", "");
        }
    }

    public void doChangePassword(Context ctx) {
        String token = ctx.cookieStore("authToken");
        String newPassword = ctx.formParam("newPassword");

        int respCode = customerService.changePassword(token, newPassword);

        ctx.status(respCode);
    }

    public void getOrders(Context ctx) {
        // TODO: orders
        ctx.status(501);
    }

    public void doPlaceOrder(Context ctx) {
        String token = ctx.cookieStore("authToken");
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
        String token = ctx.cookieStore("authToken");

        Pair<Integer, Customer> getResponse = customerService.getByToken(token);

        ctx.json(getResponse.getRight());
        ctx.status(getResponse.getLeft());
    }
}
