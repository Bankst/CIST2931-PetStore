package com.cist2931.petstore.application.customer;

import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

    @RequestMapping(value = "/api/v1/login", method = { RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CustomerLoginResponse login(
            @RequestParam(name="email", defaultValue = "") String email,
            @RequestParam(name="password", defaultValue = "") String password
            ) {

        Customer customer = null;
        CustomerLoginResponseCode responseCode = CustomerLoginResponseCode.BAD_LOGIN;

        if (!email.isEmpty() && !password.isEmpty()) {
            customer = getCustomerByEmail(email);

            if (customer != null) {
                if (checkPassword(password, customer.getPassword())) {
                    responseCode = CustomerLoginResponseCode.OK;
                } else {
                    responseCode = CustomerLoginResponseCode.BAD_LOGIN;
                }
            } else {
                responseCode = CustomerLoginResponseCode.NO_ACCOUNT;
            }
        }
        return new CustomerLoginResponse(responseCode, customer);
    }

    private Customer getCustomerByEmail(String email) {
        return null;
    }

    private boolean checkPassword(String requestPassword, String dbPassword) {
        return (requestPassword.equals(dbPassword));
    }
}
