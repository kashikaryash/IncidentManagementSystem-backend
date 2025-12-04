package com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Incident;

public interface EndUserIncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByCreatedBy(String createdBy);
    
    List<Incident> findByCallerName(String callerName);

}
