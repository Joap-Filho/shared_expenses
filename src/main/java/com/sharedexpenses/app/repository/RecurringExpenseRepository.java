package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    List<RecurringExpense> findByExpenseSpaceId(Long expenseSpaceId);
    List<RecurringExpense> findByPaidById(Long paidById);
}
