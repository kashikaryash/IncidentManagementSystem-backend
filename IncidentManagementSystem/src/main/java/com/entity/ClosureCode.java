package com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "closure_codes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClosureCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private boolean active = true;

    @Column(name = "is_default")
    private boolean isDefault = false;
}
