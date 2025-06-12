package com.dakirr.homework3.payments.apllication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dakirr.homework3.payments.domain.repository.BankAccountRepository;
import com.dakirr.homework3.payments.domain.value_object.BankAccount;

@Service
public class BankAccountService {
    private BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }


    public BankAccount get(int id) {
        return bankAccountRepository.get(id);
    }
    
    public Integer add(int id, double balance) {
        return bankAccountRepository.add(id, balance);
    }

    public Integer update(int id, double delta) {
        return bankAccountRepository.update(id, delta);
    }

    public Integer delete(int id) {
        return bankAccountRepository.delete(id);
    }
    
}
