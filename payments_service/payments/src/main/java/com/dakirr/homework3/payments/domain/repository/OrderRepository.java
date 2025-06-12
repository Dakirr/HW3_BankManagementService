package com.dakirr.homework3.payments.domain.repository;

import org.springframework.stereotype.Repository;

import com.dakirr.homework3.payments.domain.value_object.Order;

@Repository
public interface OrderRepository {
    public Order get(int id);
    public Order get_from_queue();
    public Integer add(Order paymentRequest);
}
