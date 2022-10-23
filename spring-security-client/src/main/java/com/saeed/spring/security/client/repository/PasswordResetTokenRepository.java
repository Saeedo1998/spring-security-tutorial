package com.saeed.spring.security.client.repository;

import com.saeed.spring.security.client.entity.PasswordReseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordReseToken,Long>{

    PasswordReseToken findByToken(String token);
}
