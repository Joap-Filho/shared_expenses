package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.ExpenseSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseSpaceRepository extends JpaRepository<ExpenseSpace, Long> {
    
    @Query("SELECT es FROM ExpenseSpace es JOIN es.participants p WHERE p.user.id = :userId")
    List<ExpenseSpace> findByUserParticipant(@Param("userId") Long userId);
}

