package com.dakirr.homework3.payments.domain.repository;

import org.springframework.stereotype.Repository;

import com.dakirr.homework3.payments.domain.value_object.BankAccount;

@Repository
public interface BankAccountRepository {
    public BankAccount get(int id);
    public Integer add(int id, double balance);
    public Integer update(int id, double delta);
    public Integer delete(int id);
}
