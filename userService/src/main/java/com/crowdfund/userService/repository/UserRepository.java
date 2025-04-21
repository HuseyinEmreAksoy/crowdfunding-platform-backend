package com.crowdfund.userService.repository;

import com.crowdfund.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByUsername(String username);
    public Optional<User> findByUsername(String username);

}
