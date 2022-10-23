package com.saeed.spring.security.client.repository;

import com.saeed.spring.security.client.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends JpaRepository<User,Long> {

    User findByEmail(String email);
}
