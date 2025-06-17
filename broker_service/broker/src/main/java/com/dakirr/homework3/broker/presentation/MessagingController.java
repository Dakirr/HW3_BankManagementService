package com.dakirr.homework3.broker.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.dakirr.homework3.broker.application.service.OrderService;
import com.dakirr.homework3.broker.domain.value_object.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/broker")
public class MessagingController {
    OrderService orderService;
    RestTemplate restTemplate;
    @Value("${payments.service.baseurl}") 
    private String paymentsServiceBaseUrl;
    @Value("${orders.service.baseurl}") 
    private String ordersServiceBaseUrl;
    Logger logger = LoggerFactory.getLogger(MessagingController.class);
        
    @Autowired
    public MessagingController(OrderService orderService, RestTemplate restTemplate) {
        this.orderService = orderService;
        this.restTemplate = restTemplate;
    }
    
    @PostMapping("/postOrder/OrderService")
    public ResponseEntity<Integer> postOrder_orderService(@RequestBody Order order) {
        Integer orderId = orderService.add(order);
        if (orderId != null) {
            return new ResponseEntity<>(orderId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/postOrder/PaymentService")
    @Transactional
    public ResponseEntity<Integer> postOrder_paymentService(@RequestBody Order order) {
        Integer orderId = orderService.set_status(order.getId(), order.getStatus());
        if (orderId != null) {
            orderService.stage(order.getId(), 2);
            orderService.set_status(order.getId(), order.getStatus());
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void stage_0() {
        logger.info("Attempting to process stage 0 orders.");
        
        Order order = orderService.get_by_stage(0);
        logger.info("got order:" + order);
        if (order != null) {

            orderService.stage(order.getId(), 1);
            String url = paymentsServiceBaseUrl + "/messaging/recieve";
            try {
                ResponseEntity<Integer> response = restTemplate.postForEntity(url, order, Integer.class);
                logger.info("Response status code: {}", response.getStatusCode());
                if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                    //orderService.set_status(order.getId(), 1);
                } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    orderService.set_status(order.getId(), 2);
                    orderService.stage(order.getId(), 2);
                } else {
                    orderService.set_status(order.getId(), 2);
                    orderService.stage(order.getId(), 2);
                }
            } catch (RestClientException e) {
                orderService.set_status(order.getId(), 2);
                orderService.stage(order.getId(), 2);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void stage_2() {
        Order order = orderService.get_by_stage(2);
        if (order != null) {
            String url = ordersServiceBaseUrl + "/messaging/recieve";
            try {
                ResponseEntity<Integer> response = restTemplate.postForEntity(url, order, Integer.class);
                if (response != null && response.getBody() != null && response.getStatusCode() == HttpStatus.OK) {
                    orderService.stage(order.getId(), 3);
                }
            } catch (RestClientException e) {
                orderService.stage(order.getId(), 5);
            }
        }
    }

}
