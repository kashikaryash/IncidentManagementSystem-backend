package com.specifications;

import com.entity.Incident;
import com.entity.Status;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class IncidentSpecifications {

    /**
     * Filter by incident status.
     */
    public static Specification<Incident> hasStatus(Status status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    /**
     * Filter by priority ID.
     */
    public static Specification<Incident> hasPriority(Long priorityId) {
        return (root, query, cb) ->
                priorityId == null ? null : cb.equal(root.get("priority").get("id"), priorityId);
    }

    /**
     * Filter by caller's email (unregistered users).
     */
    public static Specification<Incident> hasCallerEmail(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.like(cb.lower(root.get("callerEmail")), "%" + email.toLowerCase() + "%");
    }

    /**
     * Filter by registered caller username.
     */
    public static Specification<Incident> hasCallerUsername(String username) {
        return (root, query, cb) ->
                username == null ? null : cb.like(cb.lower(root.get("callerUser").get("username")), "%" + username.toLowerCase() + "%");
    }

    /**
     * Filter incidents created within a date range.
     */
    public static Specification<Incident> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) return cb.between(root.get("createdAt"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            return cb.lessThanOrEqualTo(root.get("createdAt"), end);
        };
    }

    /**
     * Filter incidents by short description keyword.
     */
    public static Specification<Incident> hasKeyword(String keyword) {
        return (root, query, cb) ->
                keyword == null ? null : cb.like(cb.lower(root.get("shortDescription")), "%" + keyword.toLowerCase() + "%");
    }
}
