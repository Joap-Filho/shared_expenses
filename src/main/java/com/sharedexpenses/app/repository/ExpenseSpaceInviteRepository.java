package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.ExpenseSpaceInvite;
import com.sharedexpenses.app.entity.enums.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExpenseSpaceInviteRepository extends JpaRepository<ExpenseSpaceInvite, Long> {
    Optional<ExpenseSpaceInvite> findByToken(String token);
    List<ExpenseSpaceInvite> findByExpenseSpaceIdAndStatus(Long expenseSpaceId, InviteStatus status);
}

