package com.dakirr.homework3.orders.application.service.kafka;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dakirr.homework3.orders.application.service.OrderService;
import com.dakirr.homework3.orders.domain.value_object.Order;

@Service
public class AutoKafkaService {
    OrderService orderService;
    KafkaOrderProducer kafkaOrderProducer;

    public AutoKafkaService(OrderService orderService, KafkaOrderProducer kafkaOrderProducer) {
        this.orderService = orderService;
        this.kafkaOrderProducer = kafkaOrderProducer;
    }

    @Scheduled(fixedRate = 5000)
    public void processOrder() {
        Order order = orderService.get_from_queue();
        if (order != null) {
            kafkaOrderProducer.send(order);            
        }
    }

} 
