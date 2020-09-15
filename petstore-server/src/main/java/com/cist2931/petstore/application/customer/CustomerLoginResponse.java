package com.cist2931.petstore.application.customer;

public class CustomerLoginResponse {
    private final CustomerLoginResponseCode responseCode;
    private final Customer customer;

    public CustomerLoginResponse(CustomerLoginResponseCode responseCode, Customer customer) {
        this.responseCode = responseCode;
        this.customer = customer;
    }

    public CustomerLoginResponseCode getResponseCode() {
        return responseCode;
    }

    public Customer getCustomer() {
        return customer;
    }
}
