package com.dakirr.homework3.payments.application.service;

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

    public Integer add_to_inner_queue(Order order) {
        return paymentRequestRepository.add_to_inner_queue(order);
    }

    public Order get_from_inner_queue() {
        return paymentRequestRepository.get_from_inner_queue();
    }
    
    public Integer add(Order paymentRequest) {
        return paymentRequestRepository.add(paymentRequest);
    }

    public Integer set_status(int id, int status) {
        return paymentRequestRepository.set_status(id, status);
    }
}
