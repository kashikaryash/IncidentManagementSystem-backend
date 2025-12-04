package com.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "assignment_group")
@Data
public class AssignmentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
