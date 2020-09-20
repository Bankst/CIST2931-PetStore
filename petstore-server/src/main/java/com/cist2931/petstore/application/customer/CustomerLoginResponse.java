package com.cist2931.petstore.application.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CustomerLoginResponse {
    @JsonIgnore
    private final int responseCode;
    private final String token;
    private final Customer customer;

    public CustomerLoginResponse(int responseCode, String token, Customer customer) {
        this.responseCode = responseCode;
        this.token = token;
        this.customer = customer;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getToken() {
        return token;
    }
}
