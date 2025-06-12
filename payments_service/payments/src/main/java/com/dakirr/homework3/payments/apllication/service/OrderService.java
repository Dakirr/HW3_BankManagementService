package com.dakirr.homework3.payments.apllication.service;

import org.springframework.stereotype.Service;

import com.dakirr.homework3.payments.domain.repository.OrderRepository;
import com.dakirr.homework3.payments.domain.value_object.Order;

@Service
public class OrderService {
    OrderRepository paymentRequestRepository;

    public OrderService(OrderRepository paymentRequestRepository) {
        this.paymentRequestRepository = paymentRequestRepository;
    }

    public Order get(int id) {
        return paymentRequestRepository.get(id);
    }    
    
    public Order get_from_queue() {
        return paymentRequestRepository.get_from_queue();
    }
    
    public Integer add(Order paymentRequest) {
        return paymentRequestRepository.add(paymentRequest);
    }
}
