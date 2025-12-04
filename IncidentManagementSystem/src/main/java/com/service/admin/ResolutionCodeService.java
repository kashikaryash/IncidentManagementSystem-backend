package com.service.admin;

import com.entity.ResolutionCode;
import com.repository.ResolutionCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class ResolutionCodeService {

    private final ResolutionCodeRepository repository;

    public ResolutionCodeService(ResolutionCodeRepository repository) {
        this.repository = repository;
    }

    public List<ResolutionCode> getAll() {
        return repository.findAll();
    }

    public ResolutionCode getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resolution code not found"));
    }

    public ResolutionCode create(ResolutionCode code) {
        code.setActive(true);
        return repository.save(code);
    }

    public ResolutionCode toggleActive(Long id) {
        ResolutionCode code = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resolution code not found"));
        code.setActive(!code.isActive());
        return repository.save(code);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resolution code not found");
        }
        repository.deleteById(id);
    }

    public ResolutionCode update(Long id, String newName, boolean active) {
        ResolutionCode code = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resolution code not found"));
        code.setCodeName(newName);
        code.setActive(active);
        return repository.save(code);
    }
}
