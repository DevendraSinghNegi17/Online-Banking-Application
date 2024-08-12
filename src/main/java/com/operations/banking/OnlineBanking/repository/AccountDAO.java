package com.operations.banking.OnlineBanking.repository;

import com.operations.banking.OnlineBanking.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDAO extends JpaRepository<Accounts , Integer> {

}
