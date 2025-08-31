package com.sharedexpenses.app.service;

import com.sharedexpenses.app.dto.InviteInfoResponse;
import com.sharedexpenses.app.dto.PendingRequestResponse;
import com.sharedexpenses.app.entity.ExpenseSpaceInvite;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InviteLinkOrchestrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(InviteLinkOrchestrationService.class);
    
    @Autowired
    private InviteService inviteService;
    
    @Autowired
    private ExpenseParticipantService expenseParticipantService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Obtém informações do convite (endpoint público)
     */
    public InviteInfoResponse getInviteInfo(String token) {
        logger.debug("Getting invite info for token: {}", token);
        return inviteService.getInviteInfo(token);
    }
    
    /**
     * Processa solicitação de entrada no grupo
     */
    public void requestToJoin(String token, String userEmail) {
        logger.debug("Processing join request for token {} by user {}", token, userEmail);
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
        inviteService.requestToJoin(token, user.getId());
        
        logger.info("Join request submitted for token {} by user {}", token, userEmail);
    }
    
    /**
     * Lista solicitações pendentes com validação de permissão
     */
    public List<PendingRequestResponse> getPendingRequests(Long expenseSpaceId, String userEmail) {
        logger.debug("Getting pending requests for expense space {} by user {}", expenseSpaceId, userEmail);
        
        // Validar permissões
        authorizationService.validateInviteCreationPermission(userEmail, expenseSpaceId);
        
        return inviteService.getPendingRequests(expenseSpaceId);
    }
    
    /**
     * Aprova uma solicitação de entrada
     */
    public void approveRequest(Long inviteId, String approverEmail) {
        logger.debug("Approving invite {} by user {}", inviteId, approverEmail);
        
        // Buscar usuário aprovador
        User approver = userRepository.findByEmail(approverEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
        // Buscar convite para validar permissões
        ExpenseSpaceInvite invite = inviteService.getInviteById(inviteId);
        
        // Validar permissões
        authorizationService.validateInviteCreationPermission(approverEmail, invite.getExpenseSpaceId());
        
        // Aprovar convite
        inviteService.approveRequest(inviteId, approver.getId());
        
        // Adicionar usuário ao grupo
        expenseParticipantService.addUserToExpenseSpace(
            invite.getRequestedByUserId(),
            invite.getExpenseSpaceId(),
            RoleType.MEMBER
        );
        
        logger.info("Invite {} approved by user {} and user added to group", inviteId, approverEmail);
    }
    
    /**
     * Rejeita uma solicitação de entrada
     */
    public void rejectRequest(Long inviteId, String rejecterEmail) {
        logger.debug("Rejecting invite {} by user {}", inviteId, rejecterEmail);
        
        // Buscar usuário que está rejeitando
        User rejecter = userRepository.findByEmail(rejecterEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
        // Buscar convite para validar permissões
        ExpenseSpaceInvite invite = inviteService.getInviteById(inviteId);
        
        // Validar permissões
        authorizationService.validateInviteCreationPermission(rejecterEmail, invite.getExpenseSpaceId());
        
        // Rejeitar convite
        inviteService.rejectRequest(inviteId, rejecter.getId());
        
        logger.info("Invite {} rejected by user {}", inviteId, rejecterEmail);
    }
}
