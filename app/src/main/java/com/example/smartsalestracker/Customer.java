package com.example.smartsalestracker;

public class Customer {

    String phoneNumber,customerOrder,customerPeriod,customerName;

    public Customer(){}

    public Customer(String customerName, String customerNumber, String customerOrder, String customerPeriod) {
        this.customerName = customerName;
        this.phoneNumber = customerNumber;
        this.customerOrder = customerOrder;
        this.customerPeriod = customerPeriod;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(String customerOrder) {
        this.customerOrder = customerOrder;
    }

    public String getCustomerPeriod() {
        return customerPeriod;
    }

    public void setCustomerPeriod(String customerPeriod) {
        this.customerPeriod = customerPeriod;
    }


}
