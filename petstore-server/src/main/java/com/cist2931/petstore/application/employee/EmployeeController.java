package com.cist2931.petstore.application.employee;

import com.cist2931.petstore.logging.Logger;
import io.javalin.http.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;

public class EmployeeController {

    private static final Logger logger = new Logger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(Connection dbConnection) {
        employeeService = new EmployeeService(dbConnection);
    }

    public void doCreate(Context ctx) {
        String username = ctx.queryParam("username");
        String password = ctx.queryParam("password");
    }

    public void doLogin(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        Pair<Integer, String> loginResponse = employeeService.login(username, password);
        ctx.status(loginResponse.getLeft());

        if (loginResponse.getLeft() == HttpStatus.OK_200) {
            ctx.cookieStore("authToken", loginResponse.getRight());
        }
    }

    public void doLogout(Context ctx) {
        String token = ctx.cookieStore("authToken");

        int respCode = employeeService.logout(token);

        ctx.status(respCode);

        if (respCode == HttpStatus.OK_200) {
            ctx.cookieStore("authToken", "");
        }
    }

    public void doChangePassword(Context ctx) {
        String token = ctx.cookieStore("authToken");
        String newPassword = ctx.formParam("newPassword");

        int respCode = employeeService.changePassword(token, newPassword);

        ctx.status(respCode);
    }

    public void getEmployee(Context ctx) {
        String token = ctx.cookieStore("authToken");

        Pair<Integer, Employee> getResponse = employeeService.getByToken(token);

        ctx.json(getResponse.getRight());
        ctx.status(getResponse.getLeft());
    }

}
