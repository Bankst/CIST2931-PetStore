package com.cist2931.petstore.application;

import com.cist2931.petstore.application.customer.CustomerController;
import com.cist2931.petstore.application.employee.EmployeeController;
import com.cist2931.petstore.application.merchandise.MerchandiseController;
import com.cist2931.petstore.logging.Logger;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.core.JavalinConfig;
import io.javalin.core.security.Role;
import io.javalin.core.security.SecurityUtil;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.CookieStore;

import java.sql.Connection;
import java.util.Set;


public class RestServer {

    public static final String API_URL = "/api/v1";
    public static final String CUSTOMER_API_URL = API_URL + "/customer";
    public static final String EMPLOYEE_API_URL = API_URL + "/employee";
    public static final String MERCHANDISE_API_URL = API_URL + "/merchandise";

    // This will be handled by the according customer and employee APIs
    // the business object "Order" will be common between them
    // public static final String ORDER_API_URL = API_URL + "/orders";

    private static final Logger logger = new Logger(RestServer.class);

    private enum UserRole implements Role {
        ANYONE,
        CUSTOMER,
        EMPLOYEE
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final Javalin app;
    private final AuthenticationService authService;
    private final CustomerController customerController;
    private final EmployeeController employeeController;
    private final MerchandiseController merchandiseController;

    public RestServer(int port, Connection dbConnection) {
        CookieStore.Companion.setCOOKIE_NAME("petstore");

        app = Javalin.create(this::configure).start(port);
        authService = new AuthenticationService(dbConnection);
        customerController = new CustomerController(dbConnection);
        employeeController = new EmployeeController(dbConnection);
        merchandiseController = new MerchandiseController(dbConnection);
        app.routes(this::addEndpoints);
    }

    private UserRole getUserRole(Context ctx) {
        String authToken = ctx.cookieStore("authToken");
        if (authToken != null && !authToken.isEmpty()) {
            if (ctx.path().startsWith(CUSTOMER_API_URL)) {
                int cid = authService.authorizeCustomer(authToken);
                if (cid > -1) return UserRole.CUSTOMER;
            } else if (ctx.path().startsWith(EMPLOYEE_API_URL)) {
                return UserRole.EMPLOYEE;
            } else {
                return UserRole.ANYONE;
            }
        }
        return UserRole.ANYONE;
    }

    private void configure(JavalinConfig config) {
        config.requestLogger(
                (ctx, ms) ->
                        logger.debug(
                                "Handled HTTP "
                                        + ctx.req.getMethod()
                                        + " request for path "
                                        + ctx.path()
                                        + " from "
                                        + ctx.req.getRemoteAddr()
                                        + " in "
                                        + ms.toString()
                                        + "ms"));
        config.showJavalinBanner = false;
        config.accessManager((handler, ctx, permittedRoles) -> {
            UserRole userRole = getUserRole(ctx);
            if (permittedRoles.contains(userRole)) {
                handler.handle(ctx);
            } else {
                ctx.status(401).result("Unauthorized");
            }
        });
        config.addStaticFiles("./petstore-client", Location.EXTERNAL);
    }

    private void addEndpoints() {
        final Set<Role> ANYONE_ROLE = SecurityUtil.roles(UserRole.ANYONE, UserRole.CUSTOMER, UserRole.EMPLOYEE);
        final Set<Role> CUSTOMER_ROLE = SecurityUtil.roles(UserRole.CUSTOMER);
        final Set<Role> EMPLOYEE_ROLE = SecurityUtil.roles(UserRole.EMPLOYEE);

        ApiBuilder.path(API_URL, () -> {
            ApiBuilder.put("customer", customerController::doCreate, ANYONE_ROLE);
            ApiBuilder.post("customer/login", customerController::doLogin, ANYONE_ROLE);
            ApiBuilder.path("customer", () -> {
                ApiBuilder.get(customerController::getCustomer, CUSTOMER_ROLE);
                ApiBuilder.post("logout", customerController::doLogout, CUSTOMER_ROLE);
                ApiBuilder.post("changePassword", customerController::doChangePassword, CUSTOMER_ROLE);
                ApiBuilder.put("placeOrder", customerController::doPlaceOrder, CUSTOMER_ROLE);
                ApiBuilder.get("getOrders", customerController::getOrders, CUSTOMER_ROLE);
                ApiBuilder.post("updateInfo", customerController::doUpdateInfo, CUSTOMER_ROLE);
            });
            ApiBuilder.put("employee", employeeController::doCreate, ANYONE_ROLE);
            ApiBuilder.post("employee/login", employeeController::doLogin, ANYONE_ROLE);
            ApiBuilder.path("employee", () -> {
                ApiBuilder.get(employeeController::getEmployee, EMPLOYEE_ROLE);
                ApiBuilder.post("logout", employeeController::doLogout, EMPLOYEE_ROLE);
                ApiBuilder.post("changePassword", employeeController::doChangePassword, EMPLOYEE_ROLE);
                ApiBuilder.get("getOrders", employeeController::getOrders, EMPLOYEE_ROLE);
            });
            ApiBuilder.put("merchandise", merchandiseController::doCreate, ANYONE_ROLE);
            ApiBuilder.path("merchandise", () -> {
               ApiBuilder.get(merchandiseController::getMerchandise, ANYONE_ROLE);
               ApiBuilder.post("updateInfo", merchandiseController::doUpdateInfo, ANYONE_ROLE);
            });
        });
    }
}
