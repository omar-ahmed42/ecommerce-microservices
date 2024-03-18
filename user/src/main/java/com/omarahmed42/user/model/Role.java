package com.omarahmed42.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    private Integer id;
    
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
