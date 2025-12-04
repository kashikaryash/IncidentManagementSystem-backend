package com.repository;

import com.entity.ClosureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClosureCodeRepository extends JpaRepository<ClosureCode, Long> {
}
