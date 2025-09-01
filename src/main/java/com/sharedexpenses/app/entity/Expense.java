package com.sharedexpenses.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.sharedexpenses.app.entity.enums.ExpenseType;
import com.sharedexpenses.app.entity.enums.ExpenseStatus;

@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue;

    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseStatus status = ExpenseStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private User paidBy;

    @Column(name = "include_payer_in_split", nullable = false)
    private boolean includePayerInSplit = true;

    @ManyToOne
    @JoinColumn(name = "expense_space_id", nullable = false)
    private ExpenseSpace expenseSpace;

    @ManyToOne
    @JoinColumn(name = "recurring_expense_id")
    private RecurringExpense recurringExpense;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card; // Cartão associado à despesa (opcional)

    @Column(name = "bill_due_date")
    private LocalDate billDueDate; // Data de vencimento da fatura (quando vinculada a cartão)

    @Column(name = "bill_period")
    private String billPeriod; // Período da fatura no formato "YYYY-MM" (ex: "2025-09")

    @ManyToMany
    @JoinTable(
        name = "expense_beneficiary",
        joinColumns = @JoinColumn(name = "expense_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> beneficiaries = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ExpenseType getType() { return type; }
    public void setType(ExpenseType type) { this.type = type; }

    public ExpenseStatus getStatus() { return status; }
    public void setStatus(ExpenseStatus status) { this.status = status; }

    public User getPaidBy() { return paidBy; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }

    public boolean isIncludePayerInSplit() { return includePayerInSplit; }
    public void setIncludePayerInSplit(boolean includePayerInSplit) { this.includePayerInSplit = includePayerInSplit; }

    public ExpenseSpace getExpenseSpace() { return expenseSpace; }
    public void setExpenseSpace(ExpenseSpace expenseSpace) { this.expenseSpace = expenseSpace; }

    public RecurringExpense getRecurringExpense() { return recurringExpense; }
    public void setRecurringExpense(RecurringExpense recurringExpense) { this.recurringExpense = recurringExpense; }

    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }

    public LocalDate getBillDueDate() { return billDueDate; }
    public void setBillDueDate(LocalDate billDueDate) { this.billDueDate = billDueDate; }

    public String getBillPeriod() { return billPeriod; }
    public void setBillPeriod(String billPeriod) { this.billPeriod = billPeriod; }

    public Set<User> getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(Set<User> beneficiaries) { this.beneficiaries = beneficiaries; }
}

