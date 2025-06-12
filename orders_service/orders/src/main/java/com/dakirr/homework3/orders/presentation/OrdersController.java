package com.dakirr.homework3.orders.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dakirr.homework3.orders.application.dto.OrderDTO;
import com.dakirr.homework3.orders.application.service.OrderService;
import com.dakirr.homework3.orders.domain.value_object.Order;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("add/order")
    public ResponseEntity<Integer> addOrder (@RequestBody OrderDTO orderDto) {
        Integer id = orderService.add(orderDto.getUserId(), orderDto.getAmount(), orderDto.getDescription());
        if (id == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("get/order/from_queue")
    public ResponseEntity<Order> getOrderFromQueue () {
        Order pr = orderService.get_from_queue();
        if (pr == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } 
        return new ResponseEntity<>(pr, HttpStatus.OK);
    }    

    @GetMapping("get/order/{id}")
    public ResponseEntity<Order> getPaymentRequest (@PathVariable int id) {
        Order pr = orderService.get(id);
        if (pr == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pr, HttpStatus.OK);
    }    
}
