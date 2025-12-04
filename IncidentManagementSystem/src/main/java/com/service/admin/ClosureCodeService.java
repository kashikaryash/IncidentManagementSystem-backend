package com.service.admin;

import com.entity.ClosureCode;
import com.repository.ClosureCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class ClosureCodeService {

    private final ClosureCodeRepository repository;

    public ClosureCodeService(ClosureCodeRepository repository) {
        this.repository = repository;
    }

    public List<ClosureCode> getAll() {
        return repository.findAll();
    }

    public ClosureCode create(ClosureCode code) {
        code.setActive(true);
        code.setDefault(false);
        return repository.save(code);
    }

    @Transactional
    public ClosureCode update(Long id, ClosureCode payload) {
        ClosureCode existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Closure code not found"));
        existing.setName(payload.getName());
        existing.setActive(payload.isActive());
        existing.setDefault(payload.isDefault());
        if (existing.isDefault()) {
            // ensure only one default (unset others)
            repository.findAll().stream()
                    .filter(c -> !c.getId().equals(existing.getId()) && c.isDefault())
                    .forEach(c -> { c.setDefault(false); repository.save(c); });
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Closure code not found");
        }
        repository.deleteById(id);
    }
}
