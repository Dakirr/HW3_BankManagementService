package com.dakirr.homework3.api.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dakirr.homework3.api.application.dto.BankAccountDTO;
import com.dakirr.homework3.api.application.dto.OrderDTO;
import com.dakirr.homework3.api.domain.value_object.BankAccount;
import com.dakirr.homework3.api.domain.value_object.Order;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ApiController {
    @Value("${orders.service.baseurl}") 
    private String ordersServiceBaseUrl;
    @Value("${payments.service.baseurl}") 
    private String paymentsServiceBaseUrl;
    private final RestTemplate restTemplate;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ApiController.class);
    

    @Autowired
    public ApiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("add/order") 
    public ResponseEntity<Integer> addOrder (@RequestBody OrderDTO orderDto) {
        String url = ordersServiceBaseUrl + "/orders/add/order"; 
        try {
            ResponseEntity<Integer> response = restTemplate.postForEntity(url, orderDto, Integer.class); 
            return response;
        } catch (org.springframework.web.client.RestClientException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("get/order/{id}") 
    public ResponseEntity<Order> getOrder (@PathVariable int id) {
        String url = ordersServiceBaseUrl + "/orders/get/order/" + id; 
        try {
            ResponseEntity<Order> response = restTemplate.getForEntity(url, Order.class); 
            return response;
        } catch (org.springframework.web.client.RestClientException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @PostMapping("add/BankAccount")
    public ResponseEntity<Integer> addBankAccount (@RequestBody BankAccountDTO bankAccountDto) {
        String url = paymentsServiceBaseUrl + "/payments/add/BankAccount";
        try {
            ResponseEntity<Integer> response = restTemplate.postForEntity(url, bankAccountDto, Integer.class);
            return response;
        } catch (org.springframework.web.client.RestClientException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @GetMapping("get/BankAccount/{id}")
    public ResponseEntity<BankAccount> getBankAccount (@PathVariable int id) {
        String url = paymentsServiceBaseUrl + "/payments/get/BankAccount/" + id;
        try {
            ResponseEntity<BankAccount> response = new org.springframework.web.client.RestTemplate().getForEntity(url, BankAccount.class);
            return response;
        } catch (org.springframework.web.client.RestClientException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } 
    }

    @PatchMapping("update/BankAccount/{id}")
    public ResponseEntity<Void> updateBankAccount (@PathVariable int id, @RequestBody double delta) {
        String url = paymentsServiceBaseUrl + "/payments/update/BankAccount/" + id;
        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(delta), Void.class);
            return response;    
        } catch (org.springframework.web.client.RestClientException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }        
    }
}
