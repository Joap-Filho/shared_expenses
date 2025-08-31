package com.sharedexpenses.app.service;

import com.sharedexpenses.app.dto.InviteCreatedResponse;
import com.sharedexpenses.app.dto.InviteAcceptedResponse;
import com.sharedexpenses.app.dto.PendingRequestResponse;
import com.sharedexpenses.app.entity.ExpenseSpaceInvite;
import com.sharedexpenses.app.entity.ExpenseParticipant;
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
public class InviteOrchestrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(InviteOrchestrationService.class);
    
    @Autowired
    private InviteService inviteService;
    
    @Autowired
    private ExpenseParticipantService expenseParticipantService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Orquestra a criação completa de um convite com todas as validações
     */
    public InviteCreatedResponse createInvite(Long expenseSpaceId, String userEmail) {
        logger.debug("Creating invite for expense space {} by user {}", expenseSpaceId, userEmail);
        
        // Validar permissões
        authorizationService.validateInviteCreationPermission(userEmail, expenseSpaceId);
        
        // Criar convite
        ExpenseSpaceInvite invite = inviteService.createInviteWithValidation(expenseSpaceId, userEmail);
        
        // Criar resposta formatada
        InviteCreatedResponse response = new InviteCreatedResponse(
            invite.getToken(),
            "https://invite.divvyup.space/" + invite.getToken(),
            invite.getExpirationDate().toString(),
            "2 horas"
        );
        
        logger.info("Invite created successfully for expense space {} by user {}", expenseSpaceId, userEmail);
        return response;
    }
    
    /**
     * Orquestra a aceitação completa de um convite
     */
    public InviteAcceptedResponse acceptInvite(String token, String userEmail) {
        logger.debug("Accepting invite {} by user {}", token, userEmail);
        
        // Buscar usuário atual
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Validar e aceitar convite
        ExpenseSpaceInvite invite = inviteService.acceptInvite(token, user.getId());
        
        // Adicionar usuário ao grupo
        ExpenseParticipant participant = expenseParticipantService.addUserToExpenseSpace(
            user.getId(), 
            invite.getExpenseSpaceId(), 
            RoleType.MEMBER
        );
        
        // Criar resposta
        InviteAcceptedResponse response = new InviteAcceptedResponse(
            "Convite aceito com sucesso! Você agora faz parte do grupo.",
            participant.getExpenseSpace().getName(),
            participant.getRole().name()
        );

        logger.info("User {} successfully accepted invite {} and joined expense space {}", 
            userEmail, token, invite.getExpenseSpaceId());
            
        return response;
    }
    
    /**
     * Orquestra a solicitação de entrada via convite
     */
    public String requestToJoin(String token, String userEmail) {
        logger.debug("Processing join request for token {} by user {}", token, userEmail);
        
        // Buscar usuário
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Processar solicitação
        inviteService.requestToJoin(token, user.getId());
        
        logger.info("Join request processed successfully for token {} by user {}", token, userEmail);
        return "Solicitação enviada! Aguarde a aprovação do administrador do grupo.";
    }
    
    /**
     * Obtém solicitações pendentes com validação de permissão
     */
    public List<PendingRequestResponse> getPendingRequests(Long expenseSpaceId, String userEmail) {
        logger.debug("Getting pending requests for expense space {} by user {}", expenseSpaceId, userEmail);
        
        // Validar permissões
        authorizationService.validateInviteCreationPermission(userEmail, expenseSpaceId);
        
        return inviteService.getPendingRequests(expenseSpaceId);
    }
    
    /**
     * Orquestra a aprovação completa de uma solicitação
     */
    public String approveRequest(Long inviteId, String approverEmail) {
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
        
        logger.info("Request approved successfully for invite {} by user {}", inviteId, approverEmail);
        return "Solicitação aprovada! Usuário foi adicionado ao grupo.";
    }
    
    /**
     * Orquestra a rejeição de uma solicitação
     */
    public String rejectRequest(Long inviteId, String rejecterEmail) {
        logger.debug("Rejecting invite {} by user {}", inviteId, rejecterEmail);
        
        // Buscar usuário rejeitador
        User rejecter = userRepository.findByEmail(rejecterEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Buscar convite para validar permissões
        ExpenseSpaceInvite invite = inviteService.getInviteById(inviteId);
        
        // Validar permissões
        authorizationService.validateInviteCreationPermission(rejecterEmail, invite.getExpenseSpaceId());
        
        // Rejeitar convite
        inviteService.rejectRequest(inviteId, rejecter.getId());
        
        logger.info("Request rejected successfully for invite {} by user {}", inviteId, rejecterEmail);
        return "Solicitação rejeitada.";
    }
}
