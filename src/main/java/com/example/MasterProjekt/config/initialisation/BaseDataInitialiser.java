package com.example.MasterProjekt.config.initialisation;

import java.util.Arrays;

import javax.transaction.Transactional;

import com.example.MasterProjekt.model.Authority;
import com.example.MasterProjekt.model.Nutzer;
import com.example.MasterProjekt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BaseDataInitialiser {

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String username;

    private String password;

    @Autowired
    public BaseDataInitialiser(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            @Value("${authentication.basic.username}") final String username,
            @Value("${authentication.basic.password}") final String password) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.password = password;
        this.username = username;
    }

    @Transactional
    public void initAdminUser() {
        Nutzer user = userRepository.findUserrByUsername(username);

        if (user == null) {
            user = new Nutzer();
            user.setUsername(username);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setAuthorities(Arrays.asList(new Authority("admin")));

            user = userRepository.save(user);
        }
    }
}
