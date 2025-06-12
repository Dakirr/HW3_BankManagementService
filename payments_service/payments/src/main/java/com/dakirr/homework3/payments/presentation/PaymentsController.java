package com.dakirr.homework3.payments.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dakirr.homework3.payments.apllication.dto.BankAccountDTO;
import com.dakirr.homework3.payments.apllication.service.BankAccountService;
import com.dakirr.homework3.payments.apllication.service.OrderService;
import com.dakirr.homework3.payments.domain.value_object.BankAccount;
import com.dakirr.homework3.payments.domain.value_object.Order;

@RestController
@RequestMapping("/payments")
public class PaymentsController {
    OrderService paymentRequestService;
    BankAccountService bankAccountService;

    @Autowired
    public PaymentsController(OrderService paymentRequestService, BankAccountService bankAccountService) {
        this.paymentRequestService = paymentRequestService;
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("add/BankAccount")
    public ResponseEntity<Integer> addBankAccount (@RequestBody BankAccountDTO bankAccountDto) {
        int id = bankAccountService.add(bankAccountDto.getId(), bankAccountDto.getBalance());
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("get/BankAccount/{id}")
    public ResponseEntity<BankAccount> getBankAccount (@PathVariable int id) {
        BankAccount bankAccount = bankAccountService.get(id);
        if (bankAccount == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }

    @PatchMapping("update/BankAccount/{id}")
    public ResponseEntity<Void> updateBankAccount (@PathVariable int id, @RequestBody double delta) {
        if (bankAccountService.update(id, delta) == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("add/PaymentRequest")
    public ResponseEntity<Integer> addPaymentRequest (@RequestBody Order paymentRequest) {
        Integer id = paymentRequestService.add(paymentRequest);
        if (id == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("get/PaymentRequest/from_queue")
    public ResponseEntity<Order> getPaymentRequestFromQueue () {
        Order pr = paymentRequestService.get_from_queue();
        if (pr == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } 
        return new ResponseEntity<>(pr, HttpStatus.OK);
    }    

    @GetMapping("get/PaymentRequest/{id}")
    public ResponseEntity<Order> getPaymentRequest (@PathVariable int id) {
        Order pr = paymentRequestService.get(id);
        if (pr == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pr, HttpStatus.OK);
    }    
}
