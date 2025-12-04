package com.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resolution_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolutionCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeName;
    @Builder.Default
    private boolean active = true;
}
