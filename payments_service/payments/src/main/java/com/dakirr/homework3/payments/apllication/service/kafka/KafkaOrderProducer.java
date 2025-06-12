package com.dakirr.homework3.payments.apllication.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dakirr.homework3.payments.domain.value_object.Order;


@Component
public class KafkaOrderProducer {
    private final KafkaTemplate<String, Order> kafkaTemplate;

    @Autowired
    public KafkaOrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Order order) {
        kafkaTemplate.send("payment-to-order-topic", order);
    }
}