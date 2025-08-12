package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.ExpenseParticipant;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.repository.ExpenseParticipantRepository;
import com.sharedexpenses.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseParticipantService {

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    public RoleType getRoleByUserEmailAndExpenseSpaceId(String email, Long expenseSpaceId) {
        // Buscar usuário pelo email
        Optional<Long> userIdOpt = userRepository.findByEmail(email).map(u -> u.getId());

        if (userIdOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Long userId = userIdOpt.get();

        // Buscar participante pelo userId e expenseSpaceId
        Optional<ExpenseParticipant> participantOpt = expenseParticipantRepository
                .findByUserIdAndExpenseSpaceId(userId, expenseSpaceId);

        if (participantOpt.isEmpty()) {
            throw new RuntimeException("User is not a participant of this expense space");
        }

        return participantOpt.get().getRole();
    }
}
