package com.paysafe.assignment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleUseCustomerTokenRequest {
    private String singleUseCustomerToken;

    public String getSingleUseCustomerToken() {
        return singleUseCustomerToken;
    }

    public void setSingleUseCustomerToken(String singleUseCustomerToken) {
        this.singleUseCustomerToken = singleUseCustomerToken;
    }
}
