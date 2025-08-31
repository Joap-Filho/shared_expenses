package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.ExpenseParticipant;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.exception.UserAlreadyMemberException;
import com.sharedexpenses.app.repository.ExpenseParticipantRepository;
import com.sharedexpenses.app.repository.ExpenseSpaceRepository;
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

    @Autowired
    private ExpenseSpaceRepository expenseSpaceRepository;

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

    /**
     * Adiciona um usuário como membro de um espaço de despesas
     */
    public ExpenseParticipant addUserToExpenseSpace(Long userId, Long expenseSpaceId, RoleType role) {
        // Verificar se usuário já é participante
        Optional<ExpenseParticipant> existingParticipant = expenseParticipantRepository
                .findByUserIdAndExpenseSpaceId(userId, expenseSpaceId);

        if (existingParticipant.isPresent()) {
            throw new UserAlreadyMemberException("Usuário já é membro deste grupo");
        }        // Buscar entidades necessárias
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        var expenseSpace = expenseSpaceRepository.findById(expenseSpaceId)
            .orElseThrow(() -> new RuntimeException("ExpenseSpace not found"));
            
        // Criar participante
        ExpenseParticipant participant = new ExpenseParticipant();
        participant.setUser(user);
        participant.setExpenseSpace(expenseSpace);
        participant.setRole(role != null ? role : RoleType.MEMBER);
        participant.setJoinedAt(java.time.LocalDateTime.now());

        return expenseParticipantRepository.save(participant);
    }

    /**
     * Verifica se um usuário já é participante de um espaço de despesas
     */
    public boolean isUserParticipant(Long userId, Long expenseSpaceId) {
        return expenseParticipantRepository.findByUserIdAndExpenseSpaceId(userId, expenseSpaceId)
                .isPresent();
    }
}
