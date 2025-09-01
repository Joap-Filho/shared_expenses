package com.sharedexpenses.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // "Nubank", "Itaú Personnalité"

    @Column(columnDefinition = "TEXT")
    private String description; // "Cartão principal João"

    @Column(name = "due_day", nullable = false)
    private Integer dueDay; // Dia do vencimento (1-31)

    @Column(name = "closing_day", nullable = false)
    private Integer closingDay; // Dia de fechamento da fatura (1-31)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner; // Dono do cartão

    @ManyToOne
    @JoinColumn(name = "expense_space_id", nullable = false)
    private ExpenseSpace expenseSpace; // Cartão pertence ao grupo

    // Construtores
    public Card() {}

    public Card(String name, String description, Integer dueDay, User owner, ExpenseSpace expenseSpace) {
        this.name = name;
        this.description = description;
        this.dueDay = dueDay;
        this.owner = owner;
        this.expenseSpace = expenseSpace;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDueDay() { return dueDay; }
    public void setDueDay(Integer dueDay) { this.dueDay = dueDay; }

    public Integer getClosingDay() { return closingDay; }
    public void setClosingDay(Integer closingDay) { this.closingDay = closingDay; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public ExpenseSpace getExpenseSpace() { return expenseSpace; }
    public void setExpenseSpace(ExpenseSpace expenseSpace) { this.expenseSpace = expenseSpace; }
}
