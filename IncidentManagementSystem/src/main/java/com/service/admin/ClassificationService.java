package com.service.admin;

import com.entity.Classification;
import com.repository.ClassificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class ClassificationService {

    private final ClassificationRepository classificationRepo;

    public ClassificationService(ClassificationRepository classificationRepo) {
        this.classificationRepo = classificationRepo;
    }

    public Classification createClassification(Classification classification) {
        return classificationRepo.save(classification);
    }

    public List<Classification> getAllClassifications() {
        return classificationRepo.findAll();
    }

    public Classification updateClassification(Long id, Classification updated) {
        Classification existing = classificationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classification not found"));
        existing.setName(updated.getName());
        existing.setActive(updated.isActive());
        return classificationRepo.save(existing);
    }

    public void deleteClassification(Long id) {
        if (!classificationRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Classification not found");
        }
        classificationRepo.deleteById(id);
    }

    public Classification toggleActive(Long id) {
        Classification c = classificationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classification not found"));
        c.setActive(!c.isActive());
        return classificationRepo.save(c);
    }
}
