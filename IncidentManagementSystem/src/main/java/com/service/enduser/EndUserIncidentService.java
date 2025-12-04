package com.service.enduser;

import com.dto.enduser.EndUserIncidentRequest;
import com.dto.enduser.EndUserIncidentServiceInterface;
import com.entity.Attachment;
import com.entity.Category;
import com.entity.Incident;
import com.entity.Status;
import com.repository.CategoryRepository;
import com.repository.EndUserIncidentRepository;
import com.repository.IncidentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EndUserIncidentService implements EndUserIncidentServiceInterface {

    private final IncidentRepository incidentRepository;
    private final CategoryRepository categoryRepository;
    private final EndUserIncidentRepository endUserIncidentRepository;

    @Override
    public Incident create(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Override
    public Incident getById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    @Override
    public List<Incident> getAll() {
        return incidentRepository.findAll();
    }

    @Override
    public Incident update(Long id, Incident updated) {
        Incident existing = getById(id);

        existing.setShortDescription(updated.getShortDescription());
        existing.setDetailedDescription(updated.getDetailedDescription());
        existing.setCategory(updated.getCategory());
        existing.setStatus(updated.getStatus());
        existing.setWorkNotes(updated.getWorkNotes());
        existing.setCustomerComments(updated.getCustomerComments());

        return incidentRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Incident inc = getById(id);
        inc.setDeletedAt(LocalDateTime.now());
        incidentRepository.save(inc);
    }

    @Override
    public Incident createEndUserIncident(EndUserIncidentRequest req) {

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Invalid Category"));

        Incident incident = new Incident();
        incident.setCallerName(req.getCallerName());
        incident.setCallerEmail(req.getCallerEmail());
        incident.setShortDescription(req.getShortDescription());
        incident.setDetailedDescription(req.getDetailedDescription());
        incident.setCategory(category);
        incident.setStatus(Status.NEW);
        incident.setCreatedAt(LocalDateTime.now());
        incident.setCreatedBy(req.getUsername());


        List<Attachment> attachments = new ArrayList<>();

        if (req.getAttachments() != null) {
            for (MultipartFile file : req.getAttachments()) {
                Attachment att = new Attachment();
                att.setFileName(file.getOriginalFilename());
                att.setFileType(file.getContentType());
                try {
                    att.setFileData(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Error reading attachment", e);
                }
                att.setIncident(incident);
                attachments.add(att);
            }
        }

        incident.setAttachments(attachments);

        return incidentRepository.save(incident);
    }

    @Override
    public List<Incident> findByCreatedBy(String createdBy) {
        return endUserIncidentRepository.findByCreatedBy(createdBy);
    }

}
