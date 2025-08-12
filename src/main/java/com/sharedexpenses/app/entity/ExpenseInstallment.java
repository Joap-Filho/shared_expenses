package com.sharedexpenses.app.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense_installment")
public class ExpenseInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    private int number;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    private BigDecimal value;

    private boolean paid = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Expense getExpense() { return expense; }
    public void setExpense(Expense expense) { this.expense = expense; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}

