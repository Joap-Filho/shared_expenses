package com.sharedexpenses.app.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sharedexpenses.app.entity.ExpenseParticipant;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.repository.ExpenseParticipantRepository;
import com.sharedexpenses.app.repository.ExpenseSpaceRepository;

@Service
public class ExpenseSpaceService {

    @Autowired
    private ExpenseSpaceRepository expenseSpaceRepository;

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;

    public ExpenseSpace createExpenseSpace(String name, User owner) {
        ExpenseSpace expenseSpace = new ExpenseSpace();
        expenseSpace.setName(name);
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
}

