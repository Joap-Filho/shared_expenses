package com.sharedexpenses.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sharedexpenses.app.entity.enums.RecurrenceType;

@Entity
@Table(name = "recurring_expense")
public class RecurringExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrence;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private User paidBy;

    @Column(name = "include_payer_in_split", nullable = false)
    private boolean includePayerInSplit = true;

    @ManyToOne
    @JoinColumn(name = "expense_space_id", nullable = false)
    private ExpenseSpace expenseSpace;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public RecurrenceType getRecurrence() { return recurrence; }
    public void setRecurrence(RecurrenceType recurrence) { this.recurrence = recurrence; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getPaidBy() { return paidBy; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }

    public boolean isIncludePayerInSplit() { return includePayerInSplit; }
    public void setIncludePayerInSplit(boolean includePayerInSplit) { this.includePayerInSplit = includePayerInSplit; }

    public ExpenseSpace getExpenseSpace() { return expenseSpace; }
    public void setExpenseSpace(ExpenseSpace expenseSpace) { this.expenseSpace = expenseSpace; }
}

