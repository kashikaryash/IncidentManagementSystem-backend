package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private boolean active = true;

    @OneToMany(mappedBy = "role")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<User> users;

}
