package com.operations.banking.OnlineBanking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer phone_id ;

    @Column(unique = true)
    String phoneno ;

    @ManyToOne
    @JoinColumn(name = "client_id" , nullable = false)
    ClientEntity client ;


}
