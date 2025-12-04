package com.service.admin;

import com.entity.PendingReason;
import com.repository.PendingReasonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PendingReasonService {

    private final PendingReasonRepository repository;

    public PendingReasonService(PendingReasonRepository repository) {
        this.repository = repository;
    }

    public List<PendingReason> getAllReasons() {
        return repository.findAll();
    }

    public Optional<PendingReason> getReasonById(Long id) {
        return repository.findById(id);
    }

    public PendingReason createReason(PendingReason reason) {
        if (repository.existsByReason(reason.getReason())) {
            throw new IllegalArgumentException("Reason already exists");
        }
        return repository.save(reason);
    }

    public PendingReason updateReason(Long id, PendingReason updatedReason) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setReason(updatedReason.getReason());
                    existing.setActive(updatedReason.isActive());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Reason not found with id " + id));
    }

    public void deleteReason(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Reason not found with id " + id);
        }
        repository.deleteById(id);
    }
}
