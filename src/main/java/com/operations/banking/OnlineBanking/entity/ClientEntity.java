package com.operations.banking.OnlineBanking.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer client_id ;
    private String name ;
    @Column(unique = true)
    private String username ;
    private String password ;
    private LocalDate dob ;
    @OneToOne(targetEntity = Accounts.class , cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id" , referencedColumnName = "id" , unique = true)
    private Accounts account ;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhoneNumber> phonenumbers ;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emails> emails ;



    public void addEmail(Emails email){
        this.emails.add(email);
        email.setClient(this);
    }

    public void addPhoneNumber(PhoneNumber phno){
        this.phonenumbers.add(phno);
        phno.setClient(this);
    }
}

