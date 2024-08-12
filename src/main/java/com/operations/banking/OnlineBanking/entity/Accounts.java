package com.operations.banking.OnlineBanking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    @Column(unique = true)
    private String accountno ;

    private Double initialbalance ;
    private Double balance ;

    @OneToOne(mappedBy = "account")
    private ClientEntity client ;


}
