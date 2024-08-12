package com.operations.banking.OnlineBanking.service;

import com.operations.banking.OnlineBanking.DummyEnitities.UserPrincipal;
import com.operations.banking.OnlineBanking.entity.ClientEntity;
import com.operations.banking.OnlineBanking.repository.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    ClientRepo clientDao ;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClientEntity client = clientDao.findByUsername(username);
        if(client == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        return new UserPrincipal(client);
    }
}
