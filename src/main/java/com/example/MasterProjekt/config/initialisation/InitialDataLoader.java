package com.example.MasterProjekt.config.initialisation;

import java.util.Arrays;
import java.util.Optional;

import javax.transaction.Transactional;

import com.example.MasterProjekt.model.Authority;
import com.example.MasterProjekt.model.Userr;
import com.example.MasterProjekt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean isSetup = false;

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public InitialDataLoader(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        if (isSetup) {
            return;
        }

        Optional<Userr> optionalUser = userRepository.findUserByUsername("ifis");

        if (!optionalUser.isPresent()) {
            Userr user = new Userr();
            user.setUsername("ifis");
            user.setPassword(bCryptPasswordEncoder.encode("sifi"));
            user.setAuthorities(Arrays.asList(new Authority("admin")));

            user = userRepository.save(user);
        }

        isSetup = true;
    }

}
