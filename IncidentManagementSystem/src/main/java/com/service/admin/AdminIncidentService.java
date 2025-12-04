package com.service.admin;

import com.dto.admin.AdminIncidentDto;
import com.dto.admin.AdminIncidentUpdateDto;
import com.entity.Incident;
import com.entity.Status;
import com.entity.User;
import com.entity.Workgroup;
import com.entity.Priority; // New import
import com.repository.PriorityRepository; // New import
import com.repository.IncidentRepository;
import com.repository.CategoryRepository;
import com.repository.UserRepository;
import com.repository.WorkgroupRepository;
import com.service.shared.EmailService; // New import
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminIncidentService {

	private final IncidentRepository incidentRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final WorkgroupRepository workgroupRepository;
	private final PriorityRepository priorityRepository;
    private final EmailService emailService;

    public AdminIncidentService(IncidentRepository incidentRepository, CategoryRepository categoryRepository,
			UserRepository userRepository, WorkgroupRepository workgroupRepository,
            PriorityRepository priorityRepository, EmailService emailService) {
		this.incidentRepository = incidentRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.workgroupRepository = workgroupRepository;
		this.priorityRepository = priorityRepository; // Initialized
        this.emailService = emailService;
	}

	public List<AdminIncidentDto> getAllIncidents() {
		return incidentRepository.findAll().stream().map(this::mapToAdminDto).toList();
	}

	@Transactional
	public void updateIncident(Long id, AdminIncidentUpdateDto dto) {
		Incident inc = incidentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));

        // Store old values to detect assignment/priority change for notification
        User oldAssignedTo = inc.getAssignedTo();
        Priority oldPriority = inc.getPriority();
        boolean assignmentChanged = false; // Flag for notification trigger

		if (dto.getStatus() != null) {
			try {
				inc.setStatus(Status.valueOf(dto.getStatus()));
			} catch (IllegalArgumentException ex) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + dto.getStatus());
			}
		}

		if (dto.getShortDescription() != null)
			inc.setShortDescription(dto.getShortDescription());
		if (dto.getDetailedDescription() != null)
			inc.setDetailedDescription(dto.getDetailedDescription());
		if (dto.getWorkNotes() != null)
			inc.setWorkNotes(dto.getWorkNotes());
		if (dto.getCustomerComments() != null)
			inc.setCustomerComments(dto.getCustomerComments());
		if (dto.getLocation() != null)
			inc.setLocation(dto.getLocation());
		if (dto.getContactType() != null)
			inc.setContactType(dto.getContactType());

		if (dto.getCategoryId() != null) {
			var cat = categoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
			inc.setCategory(cat);
		}

		if (dto.getAssignmentGroupId() != null) {
			Workgroup wg = workgroupRepository.findById(dto.getAssignmentGroupId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workgroup not found"));
			
            if (inc.getAssignmentGroup() == null || !wg.getId().equals(inc.getAssignmentGroup().getId())) {
                assignmentChanged = true;
            }
            inc.setAssignmentGroup(wg);
		}

		if (dto.getAssignedToUserId() != null) {
			User user = userRepository.findById(dto.getAssignedToUserId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
			
            if (oldAssignedTo == null || !user.getId().equals(oldAssignedTo.getId())) {
                assignmentChanged = true;
            }
            inc.setAssignedTo(user);
		}

		if (dto.getPriorityId() != null) {
			// **FIX: Implement Priority Setting**
			Priority priority = priorityRepository.findById(dto.getPriorityId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priority not found"));
            
            if (oldPriority == null || !priority.getId().equals(oldPriority.getId())) {
                assignmentChanged = true;
            }
            inc.setPriority(priority);
		}
        
        // **Auto-set status to ASSIGNED** if assignment/priority changes on a NEW ticket
        if (inc.getStatus() == Status.NEW && (inc.getAssignedTo() != null || inc.getAssignmentGroup() != null)) {
            inc.setStatus(Status.ASSIGNED);
        }

		Incident savedIncident = incidentRepository.save(inc);
        
        // **TRIGGER 2: Notify Assigned Analyst**
        if (assignmentChanged && savedIncident.getAssignedTo() != null) {
            sendAssignmentNotification(savedIncident);
        }
	}
    
    /** Sends notification email to the assigned analyst upon assignment or re-prioritization */
    private void sendAssignmentNotification(Incident incident) {
         if (incident.getAssignedTo() == null || incident.getAssignedTo().getEmail() == null) return;

         String analystEmail = incident.getAssignedTo().getEmail();
         String subject = "🚨 Incident Assigned: INC-" + incident.getId();
         String body = String.format(
             "Dear %s,\n\n" +
             "You have been assigned incident **INC-%d** - %s.\n" +
             "Workgroup: %s\n" +
             "Priority: %s\n\n" +
             "Please review and update the status.",
             incident.getAssignedTo().getName(),
             incident.getId(),
             incident.getShortDescription(),
             incident.getAssignmentGroup() != null ? incident.getAssignmentGroup().getName() : "N/A",
             incident.getPriority() != null ? incident.getPriority().getName() : "N/A"
         );
         emailService.sendEmail(analystEmail, subject, body);
    }


	private AdminIncidentDto mapToAdminDto(Incident inc) {
		return AdminIncidentDto.builder().id(inc.getId())
				.status(inc.getStatus() != null ? inc.getStatus().name() : null)
				.priority(inc.getPriority() != null ? inc.getPriority().getName() : null)
				.impact(inc.getImpact() != null ? inc.getImpact().getLevel() : null)
				.urgency(inc.getUrgency() != null ? inc.getUrgency().getLevel() : null)
				.classification(inc.getClassification() != null ? inc.getClassification().getName() : null)
				.category(inc.getCategory() != null ? inc.getCategory().getName() : null)
				.pendingReason(inc.getPendingReason() != null ? inc.getPendingReason().getReason() : null)
				.resolutionCode(inc.getResolutionCode() != null ? inc.getResolutionCode().getCodeName() : null)
				.closureCode(inc.getClosureCode() != null ? inc.getClosureCode().getName() : null)
				.caller(inc.getCallerUser() != null ? inc.getCallerUser().getUsername() : inc.getCallerName())
				.assignmentGroup(inc.getAssignmentGroup() != null ? inc.getAssignmentGroup().getName() : null)
				.assignedTo(inc.getAssignedTo() != null ? inc.getAssignedTo().getUsername() : null)
				.shortDescription(inc.getShortDescription()).detailedDescription(inc.getDetailedDescription())
				.workNotes(inc.getWorkNotes()).customerComments(inc.getCustomerComments())
				.createdBy(inc.getCreatedByUser() != null ? inc.getCreatedByUser().getUsername() : null)
				.createdAt(inc.getCreatedAt()).deletedAt(inc.getDeletedAt()).responseDueBy(inc.getResponseDueBy())
				.resolutionDueBy(inc.getResolutionDueBy()).build();
	}
}