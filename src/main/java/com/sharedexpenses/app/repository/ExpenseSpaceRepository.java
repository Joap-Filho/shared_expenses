package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.ExpenseSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseSpaceRepository extends JpaRepository<ExpenseSpace, Long> {
}

