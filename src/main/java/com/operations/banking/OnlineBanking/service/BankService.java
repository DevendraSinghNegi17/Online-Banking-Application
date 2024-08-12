package com.operations.banking.OnlineBanking.service;
import com.operations.banking.OnlineBanking.DummyEnitities.ClientData;
import com.operations.banking.OnlineBanking.DummyEnitities.Transfer;
import com.operations.banking.OnlineBanking.DummyEnitities.UserLoginDetails;
import com.operations.banking.OnlineBanking.JWTConfig.JWTService;
import com.operations.banking.OnlineBanking.entity.Accounts;
import com.operations.banking.OnlineBanking.entity.ClientEntity;
import com.operations.banking.OnlineBanking.entity.Emails;
import com.operations.banking.OnlineBanking.entity.PhoneNumber;
import com.operations.banking.OnlineBanking.repository.AccountDAO;
import com.operations.banking.OnlineBanking.repository.ClientRepo;
import com.operations.banking.OnlineBanking.repository.EmailRepo;
import com.operations.banking.OnlineBanking.repository.PhoneRepo;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankService {

    final private AuthenticationManager authenticationManager ;
    final private ClientRepo clientDAO ;
    final private EmailRepo emailDAO ;
    final private PhoneRepo phoneDAO ;
    final private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
    final private JWTService jwtService ;
    final private AccountDAO accountDAO;

    BankService(ClientRepo clientDAO,EmailRepo emailDAO,PhoneRepo phoneDAO,
                AuthenticationManager authenticationManager,
                JWTService jwtService , AccountDAO accountDAO){
        this.clientDAO = clientDAO ;
        this.phoneDAO = phoneDAO ;
        this.emailDAO = emailDAO ;
        this.authenticationManager = authenticationManager ;
        this.jwtService = jwtService ;
        this.accountDAO = accountDAO ;
    }

    public ClientData ClientDataFormatter(ClientEntity client){
        ClientData clientData = new ClientData();
        clientData.setUsername(client.getUsername());
        clientData.setDob(client.getDob());
        clientData.setBalance(client.getAccount().getBalance());
        clientData.setEmails(
                client.getEmails()
                        .stream()
                        .map(Emails::getEmail)
                        .collect(Collectors.toList()));

        clientData.setPhoneNumberList(client.getPhonenumbers()
                .stream()
                .map(PhoneNumber::getPhoneno)
                .collect(Collectors.toList()));
        return clientData ;
    }



    public ResponseEntity<String> ClientAddToDB(ClientEntity client) {

        client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));

        client.getEmails()
                .forEach(e -> e.setClient(client));

        client.getAccount()
                .setClient(client);


        client.getPhonenumbers()
                .forEach(e -> e.setClient(client));

        clientDAO.save(client);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }



    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<String> verify(UserLoginDetails user) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));

        if(authentication.isAuthenticated()) return
                new ResponseEntity<>(jwtService.generateToken(user.getUsername()),HttpStatus.ACCEPTED);
        return new ResponseEntity<>("Wrong Username or Password",HttpStatus.NOT_FOUND);
    }



    public ResponseEntity<Object> searchUsers(LocalDate dob, String phoneno, String name, String email) {
        if(dob != null){
            List<ClientEntity> clients = clientDAO.findByDobGreaterThanEqual(dob);

            List<ClientData> response = clients.
                    stream().
                    map(this::ClientDataFormatter).
                    collect(Collectors.toList());

            return new ResponseEntity<>(response,HttpStatus.FOUND) ;
        }
        else if(phoneno != null){
            PhoneNumber userNumber = phoneDAO.findByPhoneno(phoneno);
            if(userNumber == null) return new ResponseEntity<>("Phone Number Does not exist",HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(ClientDataFormatter(userNumber.getClient()),HttpStatus.FOUND) ;

        }
        else if(name != null){
            ClientEntity client = clientDAO.findByNameStartingWith(name);
            if(client == null) return new ResponseEntity<>("User Does not exist",HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(ClientDataFormatter(client),HttpStatus.FOUND);
        }
        else if(email != null){
            Emails userMail = emailDAO.findByEmail(email);
            if(userMail == null) return new ResponseEntity<>("Email Does not exist",HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(ClientDataFormatter(userMail.getClient()),HttpStatus.FOUND);
        }
        return new ResponseEntity<>("Nothing Entered",HttpStatus.NOT_FOUND);
    }


    @Scheduled(fixedRate = 60000)
    public void BalanceUpdatingEveryFiveMinutes(){
        List<ClientEntity> clients = clientDAO.findAll();
        for(ClientEntity client : clients){
            Accounts account = client.getAccount();
            Double initialBalance = account.getInitialbalance();
            Double currentBalance = account.getBalance() ;
            currentBalance = currentBalance + (5/100.0)*currentBalance ;
            Double maxBalance = initialBalance + (207.0/100)*initialBalance ;
            System.out.println(maxBalance);
            if(currentBalance < maxBalance) {
                account.setBalance(currentBalance);
                client.setAccount(account);
                clientDAO.save(client);
            }
        }
    }



    public ClientEntity searchByName(String username) {
        return clientDAO.findByUsername(username);
    }



    public String getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName() ;
        return username ;
    }

    public String addEmailOrPhoneNumber(String phoneno, String email) {
        String currentUser = getCurrentUser() ;
        ClientEntity user = clientDAO.findByUsername(currentUser) ;
        if(email  != null){
            Emails emails = new Emails() ;
            emails.setEmail(email);
            user.addEmail(emails);
        }
        else{
            PhoneNumber phno = new PhoneNumber();
            phno.setPhoneno(phoneno);
            user.addPhoneNumber(phno);
        }

        clientDAO.save(user);
        return "Success";
    }

    @Transactional
    public String TransferFund(Transfer transfer){
        ClientEntity sender = clientDAO.findByUsername(getCurrentUser());
        ClientEntity receiver = clientDAO.findByUsername(transfer.getReceiverName());
        if(receiver == null) throw new UsernameNotFoundException("Wrong User");
        Accounts senderAccount = sender.getAccount();
        if(senderAccount.getBalance() < transfer.getAmount()){
            throw new RuntimeException("Insufficient Account Balance");
        }
        Accounts receiverAccount = receiver.getAccount();
        receiverAccount.setBalance(receiverAccount.getBalance()+transfer.getAmount());
        senderAccount.setBalance(senderAccount.getBalance()- transfer.getAmount());
        accountDAO.save(receiverAccount);
        accountDAO.save(senderAccount);
        return "Transaction Sunccessful";
    }

    public ResponseEntity<String> deleteEmailOrPhoenNumber(String phoneNo, String email) {
        String username = getCurrentUser();
    }
}
