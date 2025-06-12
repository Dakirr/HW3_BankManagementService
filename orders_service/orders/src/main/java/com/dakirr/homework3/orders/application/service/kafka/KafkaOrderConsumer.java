package com.dakirr.homework3.orders.application.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dakirr.homework3.orders.application.service.OrderService;
import com.dakirr.homework3.orders.domain.value_object.Order;

@Component
public class KafkaOrderConsumer {
    OrderService orderService;

    public KafkaOrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "payment-to-order-topic", groupId = "order-group",
                   containerFactory = "orderKafkaListenerContainerFactory")
    public void listen(Order order) {
        orderService.set_status(order.getId(), order.getStatus());
    }

}
