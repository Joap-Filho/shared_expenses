package com.sharedexpenses.app.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.sharedexpenses.app.entity.enums.RoleType;

@Entity
@Table(name = "expense_participant")
public class ExpenseParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "expense_space_id", nullable = false)
    private ExpenseSpace expenseSpace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ExpenseSpace getExpenseSpace() { return expenseSpace; }
    public void setExpenseSpace(ExpenseSpace expenseSpace) { this.expenseSpace = expenseSpace; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}

