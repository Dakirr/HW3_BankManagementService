package com.dakirr.homework3.orders.domain.value_object;

public class Order {
    int id;
    int user_id;
    double amount;
    String description;
    int status;

    public Order() {
    }

    public Order(int id, int user_id, double amount, String description, int status) {
        this.id = id;
        this.user_id = user_id;
        this.amount = amount;
        this.description = description;
        this.status = status;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
