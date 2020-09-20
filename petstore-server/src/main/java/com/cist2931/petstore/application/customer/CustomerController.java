package com.cist2931.petstore.application.customer;

import com.cist2931.petstore.application.AuthenticationService;
import io.javalin.http.Context;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;

public final class CustomerController {

    private final CustomerService customerService;

    public CustomerController(Connection dbConnection) {
        customerService = new CustomerService(dbConnection);
    }

    public void doLogin(Context ctx) {
        String email = ctx.queryParam("email");
        String password = ctx.queryParam("password");

        Pair<Integer, String> loginResponse = customerService.login(email, password);

        String tokenJson = "{\"token\": \"" + loginResponse.getRight() + "\"}";
        ctx.result(tokenJson);
        ctx.contentType("application/json");
        ctx.status(loginResponse.getLeft());
    }

    public void doLogout(Context ctx) {
        String token = AuthenticationService.cleanToken(ctx.header("AUTHORIZATION"));

        int respCode = customerService.logout(token);

        ctx.status(respCode);
    }

    public void doChangePassword(Context ctx) {
        String token = AuthenticationService.cleanToken(ctx.header("AUTHORIZATION"));
        String newPassword = ctx.queryParam("newPassword");

        int respCode = customerService.changePasssword(token, newPassword);

        ctx.status(respCode);
    }

    public void getOrders(Context ctx) {
        // TODO: orders
        ctx.status(501);
    }

    public void getCustomer(Context ctx) {
        String token = AuthenticationService.cleanToken(ctx.header("AUTHORIZATION"));

        Pair<Integer, Customer> getResponse = customerService.getByToken(token);

        ctx.json(getResponse.getRight());
        ctx.status(getResponse.getLeft());
    }
}
