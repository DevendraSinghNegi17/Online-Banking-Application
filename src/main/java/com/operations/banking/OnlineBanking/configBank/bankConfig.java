package com.operations.banking.OnlineBanking.configBank;
import com.operations.banking.OnlineBanking.JWTConfig.JwtFilters;
import com.operations.banking.OnlineBanking.service.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class bankConfig{
    @Autowired
    SecurityUserDetailsService userDetailService ;

    @Autowired
    JwtFilters jwtFilters ;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception{
        security.csrf(customizer -> customizer.disable()) ;
        security.authorizeHttpRequests(requests -> requests
                .requestMatchers("addClient" , "login").permitAll()
                .anyRequest().authenticated());
        //security.formLogin(Customizer.withDefaults());
        security.httpBasic(Customizer.withDefaults());
        //security.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        security.addFilterBefore(jwtFilters , UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public BCryptPasswordEncoder bCryptPasswordEncoder(int strength){
        return new BCryptPasswordEncoder(strength);
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder(10));
        provider.setUserDetailsService(userDetailService);
        return provider ;
    }
}
