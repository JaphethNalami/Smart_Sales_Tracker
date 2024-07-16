package com.example.smartsalestracker;

// Class representing a Customer in the smart sales tracker application
public class Customer {

    // Declaring customer-related attributes
    String phoneNumber, customerOrder, customerPeriod, customerName, customerGender;

    // Default constructor
    public Customer() {}

    // Parameterized constructor to initialize a Customer object with specific details
    public Customer(String customerName, String customerNumber, String customerOrder, String customerPeriod, String customerGender) {
        this.customerName = customerName; // Setting the customer's name
        this.phoneNumber = customerNumber; // Setting the customer's phone number
        this.customerOrder = customerOrder; // Setting the customer's order
        this.customerPeriod = customerPeriod; // Setting the customer's order period
        this.customerGender = customerGender; // Setting the customer's gender
    }

    // Getter method for customerName
    public String getCustomerName() {
        return customerName;
    }

    // Setter method for customerName
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Getter method for phoneNumber
    public String getphoneNumber() {
        return phoneNumber;
    }

    // Setter method for phoneNumber
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getter method for customerOrder
    public String getCustomerOrder() {
        return customerOrder;
    }

    // Setter method for customerOrder
    public void setCustomerOrder(String customerOrder) {
        this.customerOrder = customerOrder;
    }

    // Getter method for customerPeriod
    public String getCustomerPeriod() {
        return customerPeriod;
    }

    // Setter method for customerPeriod
    public void setCustomerPeriod(String customerPeriod) {
        this.customerPeriod = customerPeriod;
    }

    // Getter method for customerGender
    public String getCustomerGender() {
        return customerGender;
    }

    // Setter method for customerGender
    public void setCustomerGender(String customerGender) {
        this.customerGender = customerGender;
    }
}
