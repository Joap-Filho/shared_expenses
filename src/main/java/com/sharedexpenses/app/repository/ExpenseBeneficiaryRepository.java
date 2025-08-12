package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.ExpenseBeneficiary;
import com.sharedexpenses.app.entity.ExpenseBeneficiaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseBeneficiaryRepository extends JpaRepository<ExpenseBeneficiary, ExpenseBeneficiaryId> {
    List<ExpenseBeneficiary> findByExpenseId(Long expenseId);
}
