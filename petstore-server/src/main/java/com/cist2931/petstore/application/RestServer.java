package com.cist2931.petstore.application;

import com.cist2931.petstore.StringUtils;
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

    private static final Logger logger = new Logger(RestServer.class);
    public static final String API_URL = "/api/v1";
    public static final String CUSTOMER_API_URL = API_URL + "/customer";
    public static final String EMPLOYEE_API_URL = API_URL + "/employee";
    private static final Set<Role> ANYONE_ROLE = SecurityUtil.roles(UserRole.ANYONE, UserRole.CUSTOMER, UserRole.EMPLOYEE);
    private static final Set<Role> CUSTOMER_ROLE = SecurityUtil.roles(UserRole.CUSTOMER);
    private static final Set<Role> EMPLOYEE_ROLE = SecurityUtil.roles(UserRole.EMPLOYEE);

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
        String customerAuthToken = AuthenticationService.getCustomerToken(ctx);
        String employeeAuthToken = AuthenticationService.getEmployeeToken(ctx);

        if (StringUtils.hasValue(customerAuthToken) && ctx.path().startsWith(CUSTOMER_API_URL)) {
            int cid = authService.authorizeCustomer(customerAuthToken);
            if (cid > -1) return UserRole.CUSTOMER;
        } else if (StringUtils.hasValue(employeeAuthToken) && ctx.path().startsWith(EMPLOYEE_API_URL)) {
            int eid = authService.authorizeEmployee(employeeAuthToken);
            if (eid > -1) return UserRole.EMPLOYEE;
        }

        return UserRole.ANYONE;
    }

    private void configure(JavalinConfig config) {
        config.requestLogger(
                (ctx, ms) -> {
                    if (!ctx.path().contains("/api/v1")) return;
                    logger.debug(
                            "Handled HTTP "
                                    + ctx.req.getMethod()
                                    + " request for path "
                                    + ctx.path()
                                    + " from "
                                    + ctx.req.getRemoteAddr()
                                    + " in "
                                    + ms.toString()
                                    + "ms");
                });
        config.showJavalinBanner = false;
        config.accessManager((handler, ctx, permittedRoles) -> {
            UserRole userRole = getUserRole(ctx);
            if (permittedRoles.contains(userRole)) {
                handler.handle(ctx);
            } else {
                logger.info("401'd bad access");
                ctx.status(401).result("Unauthorized");
            }
        });
        config.addStaticFiles("./petstore-client", Location.EXTERNAL);
    }

    private void addEndpoints() {
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
            ApiBuilder.put("guest/placeOrder", customerController::doPlaceGuestOrder, ANYONE_ROLE);
            ApiBuilder.post("employee/login", employeeController::doLogin, ANYONE_ROLE);
            ApiBuilder.path("employee", () -> {
                ApiBuilder.get(employeeController::getEmployee, EMPLOYEE_ROLE);
                ApiBuilder.post("logout", employeeController::doLogout, EMPLOYEE_ROLE);
                ApiBuilder.get("getOrders", employeeController::getOrders, EMPLOYEE_ROLE);
                ApiBuilder.post("setOrderShipped/:orderNo", employeeController::setOrderShipped, EMPLOYEE_ROLE);
            });
            ApiBuilder.put("merchandise", merchandiseController::doCreate, ANYONE_ROLE);
            ApiBuilder.path("merchandise", () -> {
                ApiBuilder.get(merchandiseController::getAllMerchandise, ANYONE_ROLE);
                ApiBuilder.get(":merchID", merchandiseController::getSingleMerchandise, ANYONE_ROLE);
                ApiBuilder.get("category/:categoryName", merchandiseController::getCategoryMerchandise, ANYONE_ROLE);
                ApiBuilder.post("updateInfo", merchandiseController::doUpdateInfo, ANYONE_ROLE);
            });
        });
    }
}
