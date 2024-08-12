package com.operations.banking.OnlineBanking.repository;

import com.operations.banking.OnlineBanking.entity.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepo extends JpaRepository<PhoneNumber , Integer>{
    PhoneNumber findByPhoneno(String phoneno);
}
