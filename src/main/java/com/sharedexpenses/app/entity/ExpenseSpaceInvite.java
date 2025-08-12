package com.sharedexpenses.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_space_invite")
public class ExpenseSpaceInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expense_space_id", nullable = false)
    private Long expenseSpaceId;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "used", nullable = false)
    private boolean used = false;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExpenseSpaceId() { return expenseSpaceId; }
    public void setExpenseSpaceId(Long expenseSpaceId) { this.expenseSpaceId = expenseSpaceId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}

