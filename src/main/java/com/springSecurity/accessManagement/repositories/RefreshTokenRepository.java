package com.springSecurity.accessManagement.repositories;


import com.springSecurity.accessManagement.models.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByValue(String value);
}