package com.sharedexpenses.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_beneficiary")
public class ExpenseBeneficiary {

    @EmbeddedId
    private ExpenseBeneficiaryId id;

    @ManyToOne
    @MapsId("expenseId")
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public ExpenseBeneficiary() {}

    public ExpenseBeneficiary(Expense expense, User user) {
        this.expense = expense;
        this.user = user;
        this.id = new ExpenseBeneficiaryId(expense.getId(), user.getId());
    }

    // Getters e setters
    public ExpenseBeneficiaryId getId() {
    return id;
    }

    public void setId(ExpenseBeneficiaryId id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
