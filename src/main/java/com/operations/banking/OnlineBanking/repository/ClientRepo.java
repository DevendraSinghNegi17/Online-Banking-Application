package com.operations.banking.OnlineBanking.repository;

import com.operations.banking.OnlineBanking.entity.ClientEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface ClientRepo extends JpaRepository<ClientEntity , Integer> {
    List<ClientEntity> findByDobGreaterThanEqual(LocalDate dob);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ClientEntity findByUsername(String username);
    ClientEntity findByNameStartingWith(String name);
}
