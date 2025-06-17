package com.dakirr.homework3.payments.domain.repository;

import org.springframework.stereotype.Repository;

import com.dakirr.homework3.payments.domain.value_object.Order;

@Repository
public interface OrderRepository {
    public Order get(int id);
    public Order get_from_queue();
    public Integer add(Order paymentRequest);
    public Integer set_status(int id, int status);
    public Integer add_to_inner_queue(Order order);
    public Order get_from_inner_queue();
}
