package com.repository;

import com.entity.Impact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpactRepository extends JpaRepository<Impact, Long> {
}
