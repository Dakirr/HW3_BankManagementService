package com.dakirr.homework3.orders.application.service;

import org.springframework.stereotype.Service;

import com.dakirr.homework3.orders.domain.repository.OrderRepository;
import com.dakirr.homework3.orders.domain.value_object.Order;

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
    
    public Integer add(int user_id, double amount, String description) {
        Integer id = paymentRequestRepository.add(user_id, amount, description);
        return id;
    }

    public Integer set_status(int id, int status) {
        return paymentRequestRepository.set_status(id, status);
    }
}
