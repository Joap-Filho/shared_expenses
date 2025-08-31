package com.sharedexpenses.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.sharedexpenses.app.entity.enums.InviteStatus;

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

    // Novos campos para o sistema de aprovação
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status = InviteStatus.PENDING;

    @Column(name = "requested_by_user_id")
    private Long requestedByUserId;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "approved_rejected_at")
    private LocalDateTime approvedRejectedAt;

    @Column(name = "approved_rejected_by_user_id")
    private Long approvedRejectedByUserId;

    // Getters e Setters
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

    public InviteStatus getStatus() { return status; }
    public void setStatus(InviteStatus status) { this.status = status; }

    public Long getRequestedByUserId() { return requestedByUserId; }
    public void setRequestedByUserId(Long requestedByUserId) { this.requestedByUserId = requestedByUserId; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getApprovedRejectedAt() { return approvedRejectedAt; }
    public void setApprovedRejectedAt(LocalDateTime approvedRejectedAt) { this.approvedRejectedAt = approvedRejectedAt; }

    public Long getApprovedRejectedByUserId() { return approvedRejectedByUserId; }
    public void setApprovedRejectedByUserId(Long approvedRejectedByUserId) { this.approvedRejectedByUserId = approvedRejectedByUserId; }
}

