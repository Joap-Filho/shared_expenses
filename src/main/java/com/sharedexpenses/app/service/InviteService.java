package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.ExpenseSpaceInvite;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.entity.enums.InviteStatus;
import com.sharedexpenses.app.repository.ExpenseSpaceInviteRepository;
import com.sharedexpenses.app.repository.ExpenseSpaceRepository;
import com.sharedexpenses.app.repository.UserRepository;
import com.sharedexpenses.app.dto.InviteInfoResponse;
import com.sharedexpenses.app.dto.PendingRequestResponse;
import com.sharedexpenses.app.dto.InviteCreatedResponse;
import com.sharedexpenses.app.dto.InviteAcceptedResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InviteService {

    @Autowired
    private ExpenseSpaceInviteRepository inviteRepository;

    @Autowired
    private ExpenseSpaceRepository expenseSpaceRepository;

    @Autowired
    private UserRepository userRepository;

    public ExpenseSpaceInvite createInvite(Long expenseSpaceId, Long createdByUserId) {
        ExpenseSpaceInvite invite = new ExpenseSpaceInvite();
        invite.setExpenseSpaceId(expenseSpaceId);
        invite.setCreatedByUserId(createdByUserId);

        String token = UUID.randomUUID().toString().replace("-", "");
        invite.setToken(token);

        LocalDateTime now = LocalDateTime.now();
        invite.setCreatedAt(now);
        invite.setExpirationDate(now.plusHours(2)); // expira em 2h
        invite.setStatus(InviteStatus.PENDING);

        inviteRepository.save(invite);

        return invite;
    }

    public InviteInfoResponse getInviteInfo(String token) {
        ExpenseSpaceInvite invite = inviteRepository.findByToken(token)
            .orElse(null);

        if (invite == null) {
            return new InviteInfoResponse(token, null, null, null, false, "Convite não encontrado");
        }

        // Verificar se expirou
        if (invite.getExpirationDate().isBefore(LocalDateTime.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
            inviteRepository.save(invite);
            return new InviteInfoResponse(token, null, null, null, false, "Convite expirado");
        }

        // Verificar se já foi aceito/rejeitado
        if (invite.getStatus() == InviteStatus.ACCEPTED || invite.getStatus() == InviteStatus.REJECTED) {
            return new InviteInfoResponse(token, null, null, null, false, "Convite já foi processado");
        }

        // Buscar informações do grupo e criador
        ExpenseSpace expenseSpace = expenseSpaceRepository.findById(invite.getExpenseSpaceId())
            .orElse(null);
        User createdBy = userRepository.findById(invite.getCreatedByUserId())
            .orElse(null);

        if (expenseSpace == null || createdBy == null) {
            return new InviteInfoResponse(token, null, null, null, false, "Dados do convite corrompidos");
        }

        return new InviteInfoResponse(
            token, 
            expenseSpace.getId(), 
            expenseSpace.getName(), 
            createdBy.getName(), 
            true, 
            null
        );
    }

    public void requestToJoin(String token, Long userId) {
        ExpenseSpaceInvite invite = validateInviteForRequest(token);
        
        invite.setStatus(InviteStatus.REQUESTED);
        invite.setRequestedByUserId(userId);
        invite.setRequestedAt(LocalDateTime.now());
        
        inviteRepository.save(invite);
    }

    public List<PendingRequestResponse> getPendingRequests(Long expenseSpaceId) {
        List<ExpenseSpaceInvite> pendingInvites = inviteRepository
            .findByExpenseSpaceIdAndStatus(expenseSpaceId, InviteStatus.REQUESTED);

        return pendingInvites.stream()
            .map(invite -> {
                User user = userRepository.findById(invite.getRequestedByUserId()).orElse(null);
                return new PendingRequestResponse(
                    invite.getId(),
                    user != null ? user.getName() : "Usuário não encontrado",
                    user != null ? user.getEmail() : "Email não encontrado",
                    invite.getRequestedAt(),
                    invite.getToken()
                );
            })
            .collect(Collectors.toList());
    }

    public void approveRequest(Long inviteId, Long approvedByUserId) {
        ExpenseSpaceInvite invite = inviteRepository.findById(inviteId)
            .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        if (invite.getStatus() != InviteStatus.REQUESTED) {
            throw new RuntimeException("Convite não está pendente de aprovação");
        }

        invite.setStatus(InviteStatus.ACCEPTED);
        invite.setApprovedRejectedAt(LocalDateTime.now());
        invite.setApprovedRejectedByUserId(approvedByUserId);

        inviteRepository.save(invite);
    }

    public void rejectRequest(Long inviteId, Long rejectedByUserId) {
        ExpenseSpaceInvite invite = inviteRepository.findById(inviteId)
            .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        if (invite.getStatus() != InviteStatus.REQUESTED) {
            throw new RuntimeException("Convite não está pendente de aprovação");
        }

        invite.setStatus(InviteStatus.REJECTED);
        invite.setApprovedRejectedAt(LocalDateTime.now());
        invite.setApprovedRejectedByUserId(rejectedByUserId);

        inviteRepository.save(invite);
    }

    /**
     * Aceita um convite e adiciona o usuário ao grupo
     */
    public ExpenseSpaceInvite acceptInvite(String token, Long userId) {
        ExpenseSpaceInvite invite = validateInvite(token);
        
        // Verificar se usuário já é membro
        // Esta validação deveria estar no ExpenseParticipantService
        // Por enquanto vamos deixar aqui mas depois mover
        
        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);
        
        return invite;
    }

    /**
     * Cria um convite com validações adequadas
     */
    public ExpenseSpaceInvite createInviteWithValidation(Long expenseSpaceId, String userEmail) {
        // Verificar se o expense space existe
        expenseSpaceRepository.findById(expenseSpaceId)
            .orElseThrow(() -> new RuntimeException("ExpenseSpace not found"));
            
        // Buscar usuário criador
        User creator = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return createInvite(expenseSpaceId, creator.getId());
    }

    /**
     * Cria um convite completo com validações e retorna resposta formatada
     */
    public InviteCreatedResponse createCompleteInvite(Long expenseSpaceId, String userEmail) {
        ExpenseSpaceInvite invite = createInviteWithValidation(expenseSpaceId, userEmail);
        
        return new InviteCreatedResponse(
            invite.getToken(),
            "https://invite.divvyup.space/" + invite.getToken(),
            invite.getExpirationDate().toString(),
            "2 horas"
        );
    }

    /**
     * Aceita um convite completo e retorna resposta formatada
     */
    public InviteAcceptedResponse acceptCompleteInvite(String token, String userEmail) {
        // Buscar usuário
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Validar e aceitar convite
        acceptInvite(token, user.getId());
        
        // Aqui delegamos para o ExpenseParticipantService
        return null; // Será completado depois
    }

    // Métodos legados para compatibilidade
    public ExpenseSpaceInvite validateInvite(String token) {
        ExpenseSpaceInvite invite = inviteRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid invite token"));

        if (invite.getStatus() == InviteStatus.EXPIRED || 
            invite.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invite token expired");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("Invite not available for acceptance");
        }

        return invite;
    }

    private ExpenseSpaceInvite validateInviteForRequest(String token) {
        ExpenseSpaceInvite invite = inviteRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        if (invite.getExpirationDate().isBefore(LocalDateTime.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
            inviteRepository.save(invite);
            throw new RuntimeException("Convite expirado");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("Convite já foi processado");
        }

        return invite;
    }

    public void markInviteAccepted(ExpenseSpaceInvite invite) {
        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);
    }

    public ExpenseSpaceInvite getInviteById(Long inviteId) {
        return inviteRepository.findById(inviteId)
            .orElseThrow(() -> new RuntimeException("Convite não encontrado"));
    }
}

