package com.operations.banking.OnlineBanking.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Emails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer email_id ;

    @Column(unique = true)
    String email ;

    @ManyToOne(targetEntity = ClientEntity.class)
    @JoinColumn(name = "client_id" , nullable = false)
    ClientEntity client ;

}
