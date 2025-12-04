package com.repository;

import com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String token);

    Optional<User> findByUsername(String username);

    Optional<User> findByName(String name);

    List<User> findAllByName(String name);

    List<User> findByRole_Name(String roleName);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByUsernameIgnoreCase(String username);
}
