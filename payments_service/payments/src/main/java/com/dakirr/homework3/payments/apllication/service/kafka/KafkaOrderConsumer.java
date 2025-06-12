package com.dakirr.homework3.payments.apllication.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dakirr.homework3.payments.apllication.service.BankAccountService;
import com.dakirr.homework3.payments.apllication.service.OrderService;
import com.dakirr.homework3.payments.domain.value_object.BankAccount;
import com.dakirr.homework3.payments.domain.value_object.Order;

@Component
public class KafkaOrderConsumer {
    OrderService paymentService;
    BankAccountService bankAccountService;

    @Autowired
    public KafkaOrderConsumer(OrderService paymentService, BankAccountService bankAccountService) {
        this.paymentService = paymentService;
        this.bankAccountService = bankAccountService;
    }

    @KafkaListener(topics = "order-to-payment-topic", groupId = "payment-group")
    public void listen(Order pr) {
        BankAccount ba = bankAccountService.get(pr.getUserId());
        if (ba == null) {
            pr.setStatus(2);
        } else if (ba.getBalance() < pr.getAmount()) {
            pr.setStatus(2);
        } else {
            bankAccountService.update(ba.getId(), ba.getBalance() - pr.getAmount());
            pr.setStatus(1);
            paymentService.add(pr);
        }
        
    }

}
