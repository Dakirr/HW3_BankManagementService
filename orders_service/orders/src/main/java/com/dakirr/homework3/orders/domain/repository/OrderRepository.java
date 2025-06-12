package com.dakirr.homework3.orders.domain.repository;

import org.springframework.stereotype.Repository;

import com.dakirr.homework3.orders.domain.value_object.Order;

@Repository
public interface OrderRepository {
    public Order get(int id);
    public Order get_from_queue();
    public Integer add(int user_id, double amount, String description);
    public Integer set_status(int id, int status);
}
