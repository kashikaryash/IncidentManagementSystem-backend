package com.repository;

import com.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {

    List<Priority> findAllByActiveTrue();

    Optional<Priority> findByDefaultPriorityTrue();

    Optional<Priority> findByNameIgnoreCase(String name);
}
