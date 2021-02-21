package com.paysafe.assignment.model;

//this class is responsible for unmarshal from Request Payload
public class RequestDetails {
    private String paymentHandleToken;
    private String merchantRefNum;
    private String customerOperation;
    private Integer amount;
    private String currencyCode;
    private String email;
    private String merchantCustomerId;
    private String customerId;
    private boolean settleWithAuth=true;
    private boolean dupCheck=true;

    public String getEmail() {
        return email;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMerchantCustomerId() {
        return merchantCustomerId;
    }

    public void setMerchantCustomerId(String merchantCustomerId) {
        this.merchantCustomerId = merchantCustomerId;
    }

    public String getPaymentHandleToken() {
        return paymentHandleToken;
    }

    public void setPaymentHandleToken(String paymentHandleToken) {
        this.paymentHandleToken = paymentHandleToken;
    }

    public String getCustomerOperation() {
        return customerOperation;
    }

    public void setCustomerOperation(String customerOperation) {
        this.customerOperation = customerOperation;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getMerchantRefNum() {
        return merchantRefNum;
    }

    public void setMerchantRefNum(String merchantRefNum) {
        this.merchantRefNum = merchantRefNum;
    }
}
