package com.operations.banking.OnlineBanking.DummyEnitities;

import com.operations.banking.OnlineBanking.entity.Accounts;
import com.operations.banking.OnlineBanking.entity.Emails;
import com.operations.banking.OnlineBanking.entity.PhoneNumber;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
public class ClientData {
    private String username ;
    private LocalDate dob ;
    private Double balance ;
    private List<String> phoneNumberList ;
    private List<String> emails ;

}
