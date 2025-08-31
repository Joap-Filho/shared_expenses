package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.exception.InviteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    
    @Autowired
    private ExpenseParticipantService expenseParticipantService;
    
    /**
     * Verifica se o usuário tem permissão para criar convites (OWNER ou ADMIN)
     */
    public void validateInviteCreationPermission(String userEmail, Long expenseSpaceId) {
        RoleType role = expenseParticipantService.getRoleByUserEmailAndExpenseSpaceId(userEmail, expenseSpaceId);
        
        if (role != RoleType.OWNER && role != RoleType.ADMIN) {
            throw new InviteException("Acesso negado. Apenas proprietários e administradores podem criar convites.");
        }
    }
    
    /**
     * Verifica se o usuário é participante do espaço de despesas
     */
    public boolean isUserParticipantOfExpenseSpace(String userEmail, Long expenseSpaceId) {
        try {
            expenseParticipantService.getRoleByUserEmailAndExpenseSpaceId(userEmail, expenseSpaceId);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
    
    /**
     * Obtém o papel do usuário no espaço de despesas
     */
    public RoleType getUserRole(String userEmail, Long expenseSpaceId) {
        return expenseParticipantService.getRoleByUserEmailAndExpenseSpaceId(userEmail, expenseSpaceId);
    }
}
