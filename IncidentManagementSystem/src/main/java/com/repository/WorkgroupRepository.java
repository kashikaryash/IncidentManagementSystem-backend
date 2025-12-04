package com.repository;

import com.entity.Incident;
import com.entity.Workgroup;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkgroupRepository extends JpaRepository<Workgroup, Long> {

	Optional<Incident> findByName(String assignmentGroupName);
}
