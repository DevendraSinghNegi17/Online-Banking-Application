package com.operations.banking.OnlineBanking.JWTConfig;

import com.operations.banking.OnlineBanking.service.SecurityUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtFilters extends OncePerRequestFilter {

    @Autowired
    SecurityUserDetailsService securityUserDetailsService ;
    @Autowired
    JWTService jwtService ;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtHeader = request.getHeader("Authorization");
        String token = null ;
        String username = null ;
        if(jwtHeader != null && jwtHeader.startsWith("Bearer ")){
            token = jwtHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = securityUserDetailsService.loadUserByUsername(username) ;
            if(jwtService.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken tokenn =
                        new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());

                tokenn.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(tokenn);
            }
        }
        filterChain.doFilter(request,response);

    }
}
