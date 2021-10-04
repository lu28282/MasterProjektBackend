package com.example.MasterProjekt.service;

import java.util.List;

import com.example.MasterProjekt.model.Authority;
import com.example.MasterProjekt.model.SecurityUser;
import com.example.MasterProjekt.model.Userr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationProviderService implements AuthenticationProvider {

    @Autowired
    private JpaUserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        SecurityUser user = userDetailsService.loadUserByUsername(username);
        
        return checkPassword(user, password);
    }

    public void createUser(String name, String password, List<Authority> authorities) {
        Userr user = new Userr();
        user.setUsername(name);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setAuthorities(authorities);
        userDetailsService.createUser(new SecurityUser(user)); 
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }

    private Authentication checkPassword(SecurityUser user, String rawPassword) {
        if (bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    
}
