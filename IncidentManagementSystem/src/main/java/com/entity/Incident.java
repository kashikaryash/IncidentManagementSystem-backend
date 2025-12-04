package com.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "incidents")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"attachments"})
public class Incident extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incident_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String detailedDescription;

    @ManyToOne
    @JoinColumn(name = "priority_id")
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "impact_id")
    private Impact impact;

    @ManyToOne
    @JoinColumn(name = "urgency_id")
    private Urgency urgency;

    @ManyToOne
    @JoinColumn(name = "classification_id")
    private Classification classification;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "pending_reason_id")
    private PendingReason pendingReason;

    @ManyToOne
    @JoinColumn(name = "resolution_code_id")
    private ResolutionCode resolutionCode;

    @ManyToOne
    @JoinColumn(name = "closure_code_id")
    private ClosureCode closureCode;

    @ManyToOne
    @JoinColumn(name = "workgroup_id")
    private Workgroup assignmentGroup;

    @ManyToOne
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "caller_id")
    private User callerUser;

    private String callerName;
    private String callerEmail;
    private String contactNumber;
    private String contactType;
    private String location;
    private String createdBy;

    @Column(columnDefinition = "TEXT")
    private String workNotes;

    @Column(columnDefinition = "TEXT")
    private String customerComments;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    private LocalDateTime deletedAt;
    private LocalDateTime responseDueBy;
    private LocalDateTime resolutionDueBy;
    private LocalDateTime resolvedAt;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Attachment> attachments;
}
