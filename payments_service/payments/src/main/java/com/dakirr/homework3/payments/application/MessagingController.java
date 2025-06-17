package com.dakirr.homework3.payments.application;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.dakirr.homework3.payments.application.service.BankAccountService;
import com.dakirr.homework3.payments.application.service.OrderService;
import com.dakirr.homework3.payments.domain.value_object.BankAccount;
import com.dakirr.homework3.payments.domain.value_object.Order;
import com.dakirr.homework3.payments.integration.repository.Order.OrderRepositoryDB;

import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/messaging")
public class MessagingController {
    private final OrderService orderService;
    private final BankAccountService bankAccountService;
    private final RestTemplate restTemplate;
    @Value("${broker.service.baseurl}") 
    private String brokerServiceBaseUrl;
    private final Queue<Order> orderQueue = new LinkedList<>();
    private static final Logger logger = LoggerFactory.getLogger(MessagingController.class);

    @Autowired
    public MessagingController(OrderService orderService, BankAccountService bankAccountService, RestTemplate restTemplate) {
        this.orderService = orderService;
        this.restTemplate = restTemplate;
        this.bankAccountService = bankAccountService;
    }

    @Transactional    
    @Scheduled(fixedRate = 5000)
    public void send() {
        Order order;
        if (!orderQueue.isEmpty()) {
            order = orderQueue.poll();
        } else {
            order = orderService.get_from_queue();
            if (order == null) {
                return;
            } else {
                BankAccount target = bankAccountService.get(order.getUserId());
                if (target != null && target.getBalance() >= order.getAmount()) {
                    if (orderService.set_status(order.getId(), 1) != null) {
                        bankAccountService.update(target.getId(), -order.getAmount());
                        order.setStatus(1);
                    }
                } else {
                    if (orderService.set_status(order.getId(), 2) != null) {
                        order.setStatus(2);
                    }
                }
            }
        }
        String url = brokerServiceBaseUrl + "/broker/postOrder/PaymentService"; 
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, order, String.class); 
            if (response.getStatusCode() != HttpStatus.OK) { 
                orderQueue.add(order);
            }
        } catch (RestClientException e) {
            orderQueue.add(order);
        }
        
    }
    
    @PostMapping("/recieve")
    @Transactional
    public ResponseEntity<Integer> recieve(@RequestBody Order order) {
        logger.info("POST /messaging/recieve endpoint hit.");
        if (order == null) {
            logger.warn("Received null order or order with null ID in /recieve endpoint.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        logger.info("Received order {} in /recieve endpoint. Details: {}", order.getId(), order.toString());
        try {
            Integer id = orderService.add(order);
            if (id == null) {
                logger.warn("orderService.add(order) returned null for order ID: {}. Responding with BAD_REQUEST.", order.getId());
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            logger.info("Successfully added order via orderService.add(). Returning CREATED with ID: {}", id);
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Exception in /recieve endpoint while processing order ID: {}. Error: {}", order.getId(), e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ping/{id}")
    @Transactional
    public ResponseEntity<Order> pinged(@PathVariable int id) {
        Order ret = orderService.get(id);
        if (ret == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } 
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
