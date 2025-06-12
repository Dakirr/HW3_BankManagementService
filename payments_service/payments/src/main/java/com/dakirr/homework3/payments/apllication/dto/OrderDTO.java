package com.dakirr.homework3.payments.apllication.dto;

public class OrderDTO {
    int user_id;
    double amount;
    String description;

    public OrderDTO(int user_id, double amount, String description) {
        this.user_id = user_id;
        this.amount = amount;
        this.description = description;
    }
    
    public int getUserId() {
        return user_id;
    }
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
