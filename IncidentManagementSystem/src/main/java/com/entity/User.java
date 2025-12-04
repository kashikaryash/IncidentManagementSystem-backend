package com.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private boolean active = true;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "token_expiry")
    private java.time.LocalDateTime tokenExpiry;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.PERSIST)
    @JsonIgnore // <-- ADD THIS LINE
    private List<Incident> assignedIncidents;

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.PERSIST)
    @JsonIgnore // <-- ADD THIS LINE
    private List<Incident> createdIncidents;
}
