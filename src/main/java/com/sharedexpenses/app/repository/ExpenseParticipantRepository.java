package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {
    List<ExpenseParticipant> findByExpenseSpaceId(Long expenseSpaceId);
    Optional<ExpenseParticipant> findByUserIdAndExpenseSpaceId(Long userId, Long expenseSpaceId);
}
