package com.example.warehouse_management.pdp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "policies")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String resource;

    @Column(nullable = false)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Effect effect;

    @ElementCollection
    @CollectionTable(name = "policy_subjects", joinColumns = @JoinColumn(name = "policy_id"))
    @Column(name = "subject")
    private List<String> subjects;

    @ElementCollection
    @CollectionTable(name = "policy_conditions", joinColumns = @JoinColumn(name = "policy_id"))
    @Column(name = "condition")
    private List<String> conditions;

    // Constructors
    public Policy() {}

    public Policy(String name, String description, String resource, String action, Effect effect, 
                  List<String> subjects, List<String> conditions) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
        this.effect = effect;
        this.subjects = subjects;
        this.conditions = conditions;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }
}