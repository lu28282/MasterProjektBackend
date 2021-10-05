package com.example.MasterProjekt.service;

import com.example.MasterProjekt.model.SecurityUser;
import com.example.MasterProjekt.model.Nutzer;
import com.example.MasterProjekt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Nutzer user = userRepository.findUserrByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("Problem during authentication!");
        }

        return new SecurityUser(user);
    }

    @Override
    public void changePassword(String arg0, String arg1) {
        // functionality has to be implemented if needed
    }

    @Override
    public void createUser(UserDetails userDetails) {
        // functionality has to be implemented if needed
    }

    @Override
    public void deleteUser(String arg0) {
        // functionality has to be implemented if needed

    }

    @Override
    public void updateUser(UserDetails arg0) {
        // functionality has to be implemented if needed

    }

    @Override
    public boolean userExists(String arg0) {
        // functionality has to be implemented if needed
        return false;
    }

}
