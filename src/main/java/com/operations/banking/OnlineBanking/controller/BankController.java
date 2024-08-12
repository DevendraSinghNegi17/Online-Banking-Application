package com.operations.banking.OnlineBanking.controller;
import com.operations.banking.OnlineBanking.DummyEnitities.Transfer;
import com.operations.banking.OnlineBanking.DummyEnitities.UserLoginDetails;
import com.operations.banking.OnlineBanking.entity.ClientEntity;
import com.operations.banking.OnlineBanking.service.BankService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/")
public class BankController {

    BankService bankservice ;

    BankController(BankService bankservice){
        this.bankservice = bankservice ;
    }

    @PostMapping("/addClient")
    public ResponseEntity<String> CLientAddToDB(@RequestBody ClientEntity client){

        return bankservice.ClientAddToDB(client);
    }

    @GetMapping("/login")
    public ResponseEntity LoginAsClient(@RequestBody UserLoginDetails user){

        return bankservice.verify(user);
    }

    @GetMapping("/user/search")
    public ResponseEntity<Object> searchUsers(@RequestParam(required = false)LocalDate dob ,
                               @RequestParam(required = false)String phoneno ,
                               @RequestParam(required = false)String name,
                               @RequestParam(required = false)String email)
    {
        return bankservice.searchUsers(dob,phoneno,name,email);
    }

    @PutMapping("/user/modify")
    public String addEmailOrPhoneNumber(@RequestParam(required = false)String phoneno,
                                        @RequestParam(required = false)String email, HttpServletRequest request)
    {
        return bankservice.addEmailOrPhoneNumber(phoneno,email);
    }

    @GetMapping("/user/current-user")
    public String getCurrentUser(){
        return bankservice.getCurrentUser();
    }

    @PutMapping("/user/funds-transfer")
    public String TransferFund(@RequestBody Transfer transfer){
        return bankservice.TransferFund(transfer);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteEmailOrPhoneNumber(@RequestParam(required = false)String phoneNo ,
                                                           @RequestParam(required = false)String email){
        return bankservice.deleteEmailOrPhoenNumber(phoneNo,email);
    }
}
