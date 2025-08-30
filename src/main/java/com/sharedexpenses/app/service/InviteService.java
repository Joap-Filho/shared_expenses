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
        invite.setUsed(false);

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
        invite.setUsed(true); // Para compatibilidade com sistema antigo

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

    // Métodos legados para compatibilidade
    public ExpenseSpaceInvite validateInvite(String token) {
        ExpenseSpaceInvite invite = inviteRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid invite token"));

        if (invite.getStatus() == InviteStatus.EXPIRED || 
            invite.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invite token expired");
        }

        if (invite.getStatus() != InviteStatus.ACCEPTED) {
            throw new RuntimeException("Invite not approved yet");
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

    public void markInviteUsed(ExpenseSpaceInvite invite) {
        invite.setUsed(true);
        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);
    }

    public ExpenseSpaceInvite getInviteById(Long inviteId) {
        return inviteRepository.findById(inviteId)
            .orElseThrow(() -> new RuntimeException("Convite não encontrado"));
    }
}

