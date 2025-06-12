package com.dakirr.homework3.payments.apllication.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dakirr.homework3.payments.apllication.service.OrderService;
import com.dakirr.homework3.payments.domain.value_object.Order;

@Service
public class AutoKafkaService {
    OrderService orderService;
    KafkaOrderProducer kafkaOrderProducer;

    @Autowired
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
