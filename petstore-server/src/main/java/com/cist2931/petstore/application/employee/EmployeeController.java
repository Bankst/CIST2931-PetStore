package com.cist2931.petstore.application.employee;

import com.cist2931.petstore.StringUtils;
import com.cist2931.petstore.application.AuthenticationService;
import com.cist2931.petstore.logging.Logger;
import io.javalin.http.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;
import java.util.List;

public class EmployeeController {

    private static final Logger logger = new Logger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(Connection dbConnection) {
        employeeService = new EmployeeService(dbConnection);
    }

    public void doLogin(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        Pair<Integer, String> loginResponse = employeeService.login(username, password);
        ctx.status(loginResponse.getLeft());

        if (loginResponse.getLeft() == HttpStatus.OK_200) {
            AuthenticationService.storeEmployeeToken(ctx, loginResponse.getRight());
        }
    }

    public void doLogout(Context ctx) {
        String token = AuthenticationService.getEmployeeToken(ctx);

        int respCode = employeeService.logout(token);

        ctx.status(respCode);

        if (respCode == HttpStatus.OK_200) {
            AuthenticationService.storeEmployeeToken(ctx, "");
        }
    }

    public void getEmployee(Context ctx) {
        String token = AuthenticationService.getEmployeeToken(ctx);

        Pair<Integer, Employee> getResponse = employeeService.getByToken(token);

        ctx.json(getResponse.getRight());
        ctx.status(getResponse.getLeft());
    }

    public void getOrders(Context ctx) {
        String token = AuthenticationService.getEmployeeToken(ctx);

        Pair<Integer, List<EmployeeService.OrderInfo>> response = employeeService.getOrders(token);

        if (response.getLeft() == HttpStatus.OK_200) {
            ctx.json(response.getRight());
        }

        ctx.status(response.getLeft());
    }

    public void setOrderShipped(Context ctx) {
        int orderNo;
        if (!StringUtils.hasValue(ctx.pathParam("orderNo"))) {
            orderNo = -1;
        } else {
            orderNo = Integer.parseInt(ctx.pathParam("orderNo"));
        }

        int response = employeeService.setOrderStatus(orderNo, "Shipped");

        ctx.status(response);
    }
}
