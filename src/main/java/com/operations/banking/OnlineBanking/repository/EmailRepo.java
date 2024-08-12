package com.operations.banking.OnlineBanking.repository;

import com.operations.banking.OnlineBanking.entity.Emails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepo extends JpaRepository<Emails , Integer> {
    Emails findByEmail(String email);
}
