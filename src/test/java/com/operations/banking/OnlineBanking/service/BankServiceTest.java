package com.operations.banking.OnlineBanking.service;

import com.operations.banking.OnlineBanking.DummyEnitities.Transfer;
import com.operations.banking.OnlineBanking.entity.Accounts;
import com.operations.banking.OnlineBanking.entity.ClientEntity;
import com.operations.banking.OnlineBanking.repository.AccountDAO;
import com.operations.banking.OnlineBanking.repository.ClientRepo;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class) class BankServiceTest {

    @Mock
    ClientRepo clientDAO ;

    @Mock
    AccountDAO accountDAO ;
    ClientEntity sender ;
    ClientEntity receiver ;

    @Mock
    SecurityContext securityContext ;
    @Mock
    Authentication authentication ;

    @InjectMocks
    BankService bankService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("sender");
        Accounts senderAccount = new Accounts();
        senderAccount.setAccountno("123456789");
        senderAccount.setId(1);
        senderAccount.setBalance(10000.0);
        senderAccount.setInitialbalance(10000.0);
        Accounts receiverAccount = new Accounts();
        receiverAccount.setId(2);
        receiverAccount.setAccountno("0987654321");
        receiverAccount.setBalance(5000.0);
        receiverAccount.setInitialbalance(5000.0);
        sender = new ClientEntity();
        sender.setName("Sender");
        sender.setUsername("sender");
        sender.setAccount(senderAccount);
        receiver = new ClientEntity();
        receiver.setName("Receiver");
        receiver.setUsername("receiver");
        receiver.setAccount(receiverAccount);

    }

    @Test
    void SuccessfulltransferFund() {
        Transfer transfer = new Transfer();
        transfer.setReceiverName("receiver");
        transfer.setAmount(5000.0);
        when(bankService.getCurrentUser()).thenReturn("sender");
        when(clientDAO.findByUsername(transfer.getReceiverName())).thenReturn(receiver);
        when(clientDAO.findByUsername(bankService.getCurrentUser())).thenReturn(sender);

        String result = bankService.TransferFund(transfer);
        assertEquals(5000.0,sender.getAccount().getBalance());
        assertEquals(10000.0,receiver.getAccount().getBalance());
        verify(accountDAO,times(1)).save(sender.getAccount());
        verify(accountDAO,times(1)).save(receiver.getAccount());
    }


    @Test
    void userNotFound(){
        Transfer transfer = new Transfer();
        transfer.setReceiverName("usernotfound");
        transfer.setAmount(5000.0);
        when(clientDAO.findByUsername(transfer.getReceiverName())).thenReturn(null);
        when(clientDAO.findByUsername(bankService.getCurrentUser())).thenReturn(sender);

        assertThrows(UsernameNotFoundException.class , ()->{
           bankService.TransferFund(transfer);
        });

    }

    @Test
    void TestInSufficientFund(){
        Transfer transfer = new Transfer();
        transfer.setReceiverName("receiver");
        transfer.setAmount(20000.0);
        when(clientDAO.findByUsername(transfer.getReceiverName())).thenReturn(receiver);
        when(clientDAO.findByUsername(bankService.getCurrentUser())).thenReturn(sender);

        assertThrows(RuntimeException.class , ()->{
            bankService.TransferFund(transfer);
        });
    }


}