 package com.sharedexpenses.app.controller;

import com.sharedexpenses.app.dto.InviteInfoResponse;
import com.sharedexpenses.app.dto.PendingRequestResponse;
import com.sharedexpenses.app.entity.ExpenseParticipant;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.ExpenseSpaceInvite;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.service.InviteService;
import com.sharedexpenses.app.service.ExpenseParticipantService;
import com.sharedexpenses.app.repository.UserRepository;
import com.sharedexpenses.app.repository.ExpenseParticipantRepository;
import com.sharedexpenses.app.repository.ExpenseSpaceRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invite-link")
@Tag(name = "Convites via Link", description = "Sistema de convites via link com aprovação")
public class InviteLinkController {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private ExpenseParticipantService expenseParticipantService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;

    @Autowired
    private ExpenseSpaceRepository expenseSpaceRepository;

    @GetMapping("/{token}")
    @Operation(summary = "Obter informações do convite", 
               description = "Endpoint público para validar um token de convite e obter informações do grupo")
    public ResponseEntity<InviteInfoResponse> getInviteInfo(
            @Parameter(description = "Token do convite") @PathVariable String token) {
        
        InviteInfoResponse response = inviteService.getInviteInfo(token);
        
        if (response.isValid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{token}/request")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Solicitar entrada no grupo", 
               description = "Usuário autenticado solicita entrada no grupo via convite")
    public ResponseEntity<?> requestToJoin(
            @Parameter(description = "Token do convite") @PathVariable String token) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            inviteService.requestToJoin(token, user.getId());
            return ResponseEntity.ok("Solicitação enviada! Aguarde a aprovação do administrador do grupo.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending/{expenseSpaceId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Listar solicitações pendentes", 
               description = "Lista todas as solicitações de entrada pendentes para um grupo (apenas OWNER/ADMIN)")
    public ResponseEntity<?> getPendingRequests(
            @Parameter(description = "ID do espaço de despesas") @PathVariable Long expenseSpaceId) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Verificar permissão
        try {
            RoleType role = expenseParticipantService.getRoleByUserEmailAndExpenseSpaceId(userEmail, expenseSpaceId);
            if (role != RoleType.OWNER && role != RoleType.ADMIN) {
                return ResponseEntity.status(403).body("Acesso negado");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body("Usuário não é membro deste grupo");
        }

        List<PendingRequestResponse> pendingRequests = inviteService.getPendingRequests(expenseSpaceId);
        return ResponseEntity.ok(pendingRequests);
    }

    @PostMapping("/approve/{inviteId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Aprovar solicitação", 
               description = "Aprova uma solicitação de entrada e adiciona o usuário ao grupo (apenas OWNER/ADMIN)")
    public ResponseEntity<?> approveRequest(
            @Parameter(description = "ID do convite") @PathVariable Long inviteId) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User approver = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            // Buscar o convite primeiro
            ExpenseSpaceInvite invite = inviteService.getInviteById(inviteId);
            
            // Verificar se o usuário tem permissão neste grupo
            RoleType role = expenseParticipantService.getRoleByUserEmailAndExpenseSpaceId(userEmail, invite.getExpenseSpaceId());
            if (role != RoleType.OWNER && role != RoleType.ADMIN) {
                return ResponseEntity.status(403).body("Acesso negado");
            }
            
            // Aprovar o convite
            inviteService.approveRequest(inviteId, approver.getId());

            // Adicionar usuário como MEMBER
            User requestedUser = userRepository.findById(invite.getRequestedByUserId())
                .orElseThrow(() -> new RuntimeException("Usuário solicitante não encontrado"));

            ExpenseSpace expenseSpace = expenseSpaceRepository.findById(invite.getExpenseSpaceId())
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

            ExpenseParticipant participant = new ExpenseParticipant();
            participant.setUser(requestedUser);
            participant.setExpenseSpace(expenseSpace);
            participant.setRole(RoleType.MEMBER);
            participant.setJoinedAt(LocalDateTime.now());

            expenseParticipantRepository.save(participant);

            return ResponseEntity.ok("Solicitação aprovada! Usuário foi adicionado ao grupo.");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject/{inviteId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Rejeitar solicitação", 
               description = "Rejeita uma solicitação de entrada no grupo (apenas OWNER/ADMIN)")
    public ResponseEntity<?> rejectRequest(
            @Parameter(description = "ID do convite") @PathVariable Long inviteId) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User rejecter = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            // Buscar o convite primeiro
            ExpenseSpaceInvite invite = inviteService.getInviteById(inviteId);
            
            // Verificar se o usuário tem permissão neste grupo
            RoleType role = expenseParticipantService.getRoleByUserEmailAndExpenseSpaceId(userEmail, invite.getExpenseSpaceId());
            if (role != RoleType.OWNER && role != RoleType.ADMIN) {
                return ResponseEntity.status(403).body("Acesso negado");
            }
            
            inviteService.rejectRequest(inviteId, rejecter.getId());
            return ResponseEntity.ok("Solicitação rejeitada.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
