package com.sharedexpenses.app.entity;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense_space")
public class ExpenseSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "expenseSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseParticipant> participants = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public List<ExpenseParticipant> getParticipants() { return participants; }
    public void setParticipants(List<ExpenseParticipant> participants) { this.participants = participants; }
}

