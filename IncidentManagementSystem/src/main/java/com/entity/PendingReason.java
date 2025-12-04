package com.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pending_reasons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reason;
    @Builder.Default
    private boolean active = true;
}
