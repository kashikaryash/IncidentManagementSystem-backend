package com.repository;

import com.entity.PendingReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingReasonRepository extends JpaRepository<PendingReason, Long> {
    boolean existsByReason(String reason);
}
