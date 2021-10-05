package com.example.MasterProjekt.repository;

import com.example.MasterProjekt.model.Nutzer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Nutzer, Long> {
    
    Nutzer findUserrByUsername(String username);
}
