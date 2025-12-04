package com.service.analyst;

import com.dto.analyst.AnalystIncidentResponseDto;
import com.dto.shared.IncidentRequest;
import com.entity.*;
import com.repository.AttachmentRepository;
import com.repository.IncidentRepository;
import com.repository.PriorityRepository;
import com.repository.UserRepository;
import com.service.admin.AttachmentService;
import com.service.admin.CategoryService;
import com.service.shared.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);

    private final IncidentRepository incidentRepo;
    private final UserRepository userRepo;
    private final AttachmentRepository attachmentRepo;
    private final CategoryService categoryService;
    private final AttachmentService attachmentService;
    private final PriorityRepository priorityRepo;
    private final EmailService emailService;

    public IncidentService(IncidentRepository incidentRepo,
                           UserRepository userRepo,
                           AttachmentRepository attachmentRepo,
                           CategoryService categoryService,
                           AttachmentService attachmentService,
                           PriorityRepository priorityRepo,
                           EmailService emailService) {
        this.incidentRepo = incidentRepo;
        this.userRepo = userRepo;
        this.attachmentRepo = attachmentRepo;
        this.categoryService = categoryService;
        this.attachmentService = attachmentService;
        this.priorityRepo = priorityRepo;
        this.emailService = emailService;
    }

    // ===============================================================
    //                    INCIDENT CREATION
    // ===============================================================
public Incident createIncidentFromRequest(IncidentRequest req, List<MultipartFile> files) {

    var auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    String username = auth.getName();

    if (username == null || username.isBlank() || username.equals("anonymousUser")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found: " + username);
    }

    User callerUser = userRepo.findByEmailIgnoreCase(username)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user not found: " + username));

    Incident incident = new Incident();
    incident.setCallerUser(callerUser);
    incident.setStatus(Status.NEW);
    incident.setCreatedAt(LocalDateTime.now());
    incident.setUpdatedAt(LocalDateTime.now());

    incident.setShortDescription(req.getShortDescription());
    incident.setDetailedDescription(req.getDetailedDescription());
    incident.setCallerName(callerUser.getName());
    incident.setCallerEmail(callerUser.getEmail());

    Category category = null;
    if (req.getCategory() != null && !req.getCategory().isBlank()) {
        category = categoryService.findCategoryFromPath(req.getCategory());
    }
    incident.setCategory(category);

    Priority priority = (category != null) ? category.getDefaultPriority() : null;

    if (priority == null) {
        priority = priorityRepo.findByDefaultPriorityTrue()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No global default priority configured."
                ));
    }

    incident.setPriority(priority);
    setSlaTimes(incident);

    Incident saved = incidentRepo.save(incident);

    if (files != null && !files.isEmpty()) {
        attachmentService.saveFiles(saved, files);
    }

    if (saved.getCallerEmail() != null && !saved.getCallerEmail().isBlank()) {
        try {
            emailService.sendEmail(
                    saved.getCallerEmail(),
                    "Incident Created: #" + saved.getId(),
                    "Your incident has been created with ID " + saved.getId()
            );
        } catch (Exception e) {
            log.warn("Email failed: {}", e.getMessage());
        }
    }

    return saved;
}



    // ===============================================================
    //                    INCIDENT READ
    // ===============================================================
    public Incident getIncidentById(Long id) {
        return incidentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Incident not found ID: " + id));
    }

    public List<AnalystIncidentResponseDto> getAllIncidentDtos() {
        return incidentRepo.findAll().stream()
                .map(this::mapToAnalystDto)
                .collect(Collectors.toList());
    }

    // Note: called by controller with email param
    public List<AnalystIncidentResponseDto> getIncidentsByCaller(String email) {
        return incidentRepo.findByCallerEmailIgnoreCase(email)
                .stream()
                .map(this::mapToAnalystDto)
                .collect(Collectors.toList());
    }

    public List<AnalystIncidentResponseDto> getIncidentsByAssignee(String email) {
        log.debug("Fetching incidents assigned to {}", email);

        List<Incident> incidents = incidentRepo.findByAssignedTo_EmailIgnoreCase(email);

        log.info("Found {} incidents assigned to {}", incidents.size(), email);

        return incidents.stream()
                .map(this::mapToAnalystDto)
                .collect(Collectors.toList());
    }

    // ===============================================================
    //                    INCIDENT UPDATE
    // ===============================================================
    @Transactional
    public Incident updateIncidentStatus(Long id, Status status, Long assignedUserId, Long groupId) {

        Incident incident = getIncidentById(id);

        incident.setStatus(status);
        incident.setUpdatedAt(LocalDateTime.now());

        if (assignedUserId != null) {
            User assignee = userRepo.findById(assignedUserId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Assigned user not found"));
            incident.setAssignedTo(assignee);
        }

        if (groupId != null) {
            Workgroup group = new Workgroup();
            group.setId(groupId); // minimal; fetch actual if needed
            incident.setAssignmentGroup(group);
        }

        if (status == Status.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        return incidentRepo.save(incident);
    }

    @Transactional
    public void deleteIncident(Long id) {
        if (!incidentRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Incident not found ID: " + id);
        }
        incidentRepo.deleteById(id);
    }

    public Optional<Attachment> getAttachmentById(Long id) {
        return attachmentRepo.findById(id);
    }

    @Transactional
    public void deleteAttachment(Long id) {
        if (!attachmentRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found ID: " + id);
        }
        attachmentRepo.deleteById(id);
    }

    private void setSlaTimes(Incident incident) {

        Priority p = incident.getPriority();

        if (p == null || incident.getCreatedAt() == null) {
            log.warn("SLA not set due to missing priority or createdAt");
            return;
        }

        if (p.getResponseSlaMins() != null) {
            incident.setResponseDueBy(incident.getCreatedAt().plusMinutes(p.getResponseSlaMins()));
        }
        if (p.getResolutionSlaMins() != null) {
            incident.setResolutionDueBy(incident.getCreatedAt().plusMinutes(p.getResolutionSlaMins()));
        }
    }

    public AnalystIncidentResponseDto mapToAnalystDto(Incident incident) {
        AnalystIncidentResponseDto dto = new AnalystIncidentResponseDto();
        Priority priority = incident.getPriority();

        dto.setId(incident.getId());
        dto.setShortDescription(incident.getShortDescription());
        dto.setDetailedDescription(incident.getDetailedDescription());
        dto.setPriorityName(priority != null ? priority.getName() : "N/A");
        dto.setPriorityLevel(priority != null ? priority.getDisplayName() : "N/A");
        dto.setStatus(incident.getStatus().name());
        dto.setCategory(incident.getCategory() != null ? incident.getCategory().getName() : null);
        dto.setCaller(incident.getCallerUser() != null ? incident.getCallerUser().getName() : incident.getCallerName());
        dto.setAssignedToUserId(incident.getAssignedTo() != null ? incident.getAssignedTo().getId() : null);
        dto.setAssignmentGroup(incident.getAssignmentGroup() != null ? incident.getAssignmentGroup().getName() : null);
        dto.setResponseTimeRemaining(formatSlaRemaining(incident.getResponseDueBy()));
        dto.setResolutionTimeRemaining(formatSlaRemaining(incident.getResolutionDueBy()));
        dto.setCreatedAt(incident.getCreatedAt() != null ? incident.getCreatedAt().toString() : null);

        return dto;
    }

    @Transactional
    public Incident resolveIncident(Long id, String notes) {
        Incident incident = getIncidentById(id);

        incident.setStatus(Status.RESOLVED);
        incident.setResolutionNotes(notes);
        incident.setResolvedAt(LocalDateTime.now());

        Incident savedIncident = incidentRepo.save(incident);

        // send email
        if (incident.getCallerEmail() != null && !incident.getCallerEmail().isBlank()) {
            String subject = "Your Incident #" + incident.getId() + " has been resolved";
            String body = "Hi " + incident.getCallerName() + ",\n\n" +
                          "Your incident titled \"" + incident.getShortDescription() + "\" has been resolved.\n" +
                          "Resolution Notes: " + notes + "\n\n" +
                          "Thank you,\nIncident Management Team";

            try {
                emailService.sendEmail(incident.getCallerEmail(), subject, body);
            } catch (Exception ex) {
                log.warn("Failed to send resolution email for {}: {}", incident.getId(), ex.getMessage());
            }
        }

        return savedIncident;
    }

    private String formatSlaRemaining(LocalDateTime due) {
        if (due == null) return "N/A";

        Duration d = Duration.between(LocalDateTime.now(), due);
        if (d.isNegative()) return "BREACHED";

        return d.toHours() + "h " + d.toMinutesPart() + "m";
    }
}
