package com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workgroups")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workgroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String displayName;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String description;

    @Builder.Default
    private Boolean master = false;

    @Builder.Default
    private Boolean defaultWorkgroup = false;

    @Builder.Default
    private Boolean active = true;
}
