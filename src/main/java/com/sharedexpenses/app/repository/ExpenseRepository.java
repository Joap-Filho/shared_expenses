package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByExpenseSpaceId(Long expenseSpaceId);
}

