package com.dakirr.homework3.broker.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dakirr.homework3.broker.domain.repository.OrderRepository;
import com.dakirr.homework3.broker.domain.value_object.Order;

@Service
public class OrderService {
    OrderRepository orderRepository;
    
    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    public Integer add (Order order) {
        return orderRepository.add(order);
    };
    public Order get (int id) {
        return orderRepository.get(id);
    }; 
    public Integer set_status(int id, int status) {
        return orderRepository.set_status(id, status);
    }; 
    public Integer get_stage(int id) {
        return orderRepository.get_stage(id);
    }; 
    public Integer stage(int id, int stage) {
        return orderRepository.stage(id, stage);
    }; 
    public Order get_by_stage(int stage) {
        return orderRepository.get_by_stage(stage);
    }
}
