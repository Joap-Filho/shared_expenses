package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.ExpenseInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseInstallmentRepository extends JpaRepository<ExpenseInstallment, Long> {
    List<ExpenseInstallment> findByExpenseId(Long expenseId);
    List<ExpenseInstallment> findByExpenseIdOrderByNumber(Long expenseId);
    List<ExpenseInstallment> findByExpenseIdAndPaidFalse(Long expenseId);
}
