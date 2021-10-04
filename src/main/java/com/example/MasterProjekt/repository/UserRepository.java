package com.example.MasterProjekt.repository;

import java.util.Optional;

import com.example.MasterProjekt.model.Userr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Userr, Long> {
    
    Optional<Userr> findUserByUsername(String username);
}
