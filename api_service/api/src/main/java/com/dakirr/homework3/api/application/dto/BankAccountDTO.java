package com.dakirr.homework3.api.application.dto;

public class BankAccountDTO {
    int id;
    double balance;
    public BankAccountDTO(double balance) {
        this.balance = balance;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
