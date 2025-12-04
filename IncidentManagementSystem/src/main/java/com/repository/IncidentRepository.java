package com.repository;

import com.entity.Incident;
import com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Optional<Incident> findIncidentById(Long id);

    List<Incident> findByCallerUser(User callerUser);

    List<Incident> findByCallerUser_UsernameIgnoreCase(String username);

    List<Incident> findByCallerEmailIgnoreCase(String callerEmail);

    List<Incident> findByCallerNameContainingIgnoreCase(String callerName);

    List<Incident> findByAssignedTo_Username(String username);

    List<Incident> findByAssignedTo_EmailIgnoreCase(String email);
}
