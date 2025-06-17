package com.dakirr.homework3.orders.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.dakirr.homework3.orders.application.service.OrderService;
import com.dakirr.homework3.orders.domain.value_object.Order;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/messaging")
public class MessagingController {
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    @Value("${broker.service.baseurl}") 
    private String brokerServiceBaseUrl;

    public MessagingController(OrderService orderService, RestTemplate restTemplate) {
        this.orderService = orderService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void send() {
        Order order = orderService.get_from_queue();
        if (order != null) {
            String url = brokerServiceBaseUrl + "/broker/postOrder/OrderService"; 
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, order, String.class); 
                if (response.getStatusCode() != HttpStatus.CREATED) { 
                    orderService.set_status(order.getId(), 2); 
                }
            } catch (RestClientException e) {
                orderService.set_status(order.getId(), 2);
            }
        }   
    }
    
    @PostMapping("/recieve")
    public ResponseEntity<Integer> recieve(@RequestBody Order order) {
        try {
            Integer id = orderService.set_status(order.getId(), order.getStatus());
            if (id == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(order.getId(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
