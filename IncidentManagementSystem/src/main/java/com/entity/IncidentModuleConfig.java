package com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "incident_module_config")
public class IncidentModuleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean approvalRequired;
    private boolean auditTrailEnabled;

    @Column(length = 1000)
    private String customFieldsJson; 

    @Column(length = 1000)
    private String slaConfigJson;
}
