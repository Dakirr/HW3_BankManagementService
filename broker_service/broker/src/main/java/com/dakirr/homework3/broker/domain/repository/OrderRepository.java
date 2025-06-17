package com.dakirr.homework3.broker.domain.repository;

import org.springframework.stereotype.Repository;

import com.dakirr.homework3.broker.domain.value_object.Order;

@Repository
public interface OrderRepository {
    public Integer add (Order order);
    public Order get (int id); 
    public Integer set_status(int id, int status); 
    public Integer get_stage(int id); 
    public Integer stage(int id, int stage); 
    public Order get_by_stage(int stage);
}
