package com.sharedexpenses.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sharedexpenses.app.dto.ExpenseSpaceResponse;
import com.sharedexpenses.app.entity.ExpenseParticipant;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.repository.ExpenseParticipantRepository;
import com.sharedexpenses.app.repository.ExpenseSpaceRepository;
import com.sharedexpenses.app.repository.UserRepository;

@Service
public class ExpenseSpaceService {

    @Autowired
    private ExpenseSpaceRepository expenseSpaceRepository;

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    public ExpenseSpace createExpenseSpace(String name, String description, User owner) {
        ExpenseSpace expenseSpace = new ExpenseSpace();
        expenseSpace.setName(name);
        expenseSpace.setDescription(description);
        expenseSpace.setCreatedBy(owner);

        ExpenseSpace savedSpace = expenseSpaceRepository.save(expenseSpace);

        // Criar participante OWNER
        ExpenseParticipant ownerParticipant = new ExpenseParticipant();
        ownerParticipant.setExpenseSpace(savedSpace);
        ownerParticipant.setUser(owner);
        ownerParticipant.setRole(RoleType.OWNER);
        ownerParticipant.setJoinedAt(LocalDateTime.now());

        expenseParticipantRepository.save(ownerParticipant);

        return savedSpace;
    }

    @Transactional(readOnly = true)
    public List<ExpenseSpaceResponse> getUserExpenseSpaces(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseSpaceRepository.findByUserParticipant(user.getId()).stream()
                .map(space -> convertToResponse(space, user.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenseSpaceResponse getExpenseSpaceDetails(Long spaceId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        ExpenseSpace space = expenseSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Expense space not found"));

        // Verificar se o usuário é participante
        boolean isUserParticipant = expenseParticipantRepository
            .findByUserIdAndExpenseSpaceId(user.getId(), spaceId)
            .isPresent();

        if (!isUserParticipant) {
            throw new RuntimeException("User is not a participant of this expense space");
        }

        return convertToResponse(space, user.getId());
    }

    private ExpenseSpaceResponse convertToResponse(ExpenseSpace space, Long userId) {
        // Obter papel do usuário no grupo
        String userRole = expenseParticipantRepository
            .findByUserIdAndExpenseSpaceId(userId, space.getId())
            .map(participant -> participant.getRole().toString())
            .orElse("UNKNOWN");

        // Contar total de participantes
        int totalParticipants = space.getParticipants().size();

        return new ExpenseSpaceResponse(
            space.getId(),
            space.getName(), 
            space.getDescription(),
            space.getCreatedBy().getName(),
            space.getCreatedBy().getEmail(),
            userRole,
            totalParticipants
        );
    }
}

