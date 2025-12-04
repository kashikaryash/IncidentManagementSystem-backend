package com.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "impacts")
public class Impact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String level;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean isDefault = false;

    @Builder.Default
    private Integer sortOrder = 0;
}
