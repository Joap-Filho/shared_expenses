package com.sharedexpenses.app.controller;

import com.sharedexpenses.app.dto.InviteCreatedResponse;
import com.sharedexpenses.app.dto.InviteAcceptedResponse;
import com.sharedexpenses.app.exception.UserAlreadyMemberException;
import com.sharedexpenses.app.exception.InviteException;
import com.sharedexpenses.app.service.InviteOrchestrationService;
import com.sharedexpenses.app.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invites")
@Tag(name = "Convites", description = "Endpoints para gerenciar convites para espaços de despesas")
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseSpaceInviteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseSpaceInviteController.class);

    @Autowired
    private InviteOrchestrationService inviteOrchestrationService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @PostMapping("/create")
    @Operation(summary = "Criar convite", description = "Gera um token de convite para adicionar novos membros ao espaço de despesas (apenas OWNER/ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Convite criado com sucesso",
            content = @Content(schema = @Schema(implementation = InviteCreatedResponse.class))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas OWNER/ADMIN podem criar convites"),
        @ApiResponse(responseCode = "404", description = "Espaço de despesas não encontrado")
    })
    public ResponseEntity<?> createInvite(@RequestParam Long expenseSpaceId) {
        try {
            String userEmail = authenticationUtil.getCurrentUserEmail();
            InviteCreatedResponse response = inviteOrchestrationService.createInvite(expenseSpaceId, userEmail);
            return ResponseEntity.ok(response);
            
        } catch (InviteException e) {
            logger.warn("Permission denied for invite creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            
        } catch (RuntimeException e) {
            logger.error("Error creating invite for expense space {}: {}", expenseSpaceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro ao criar convite: " + e.getMessage());
        }
    }

    @PostMapping("/accept")
    @Operation(summary = "Aceitar convite", description = "Aceita um convite através do token e adiciona o usuário ao espaço de despesas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Convite aceito com sucesso",
            content = @Content(schema = @Schema(implementation = InviteAcceptedResponse.class))),
        @ApiResponse(responseCode = "400", description = "Convite inválido ou expirado"),
        @ApiResponse(responseCode = "409", description = "Usuário já é membro do grupo")
    })
    public ResponseEntity<?> acceptInvite(@RequestParam String token) {
        try {
            String userEmail = authenticationUtil.getCurrentUserEmail();
            InviteAcceptedResponse response = inviteOrchestrationService.acceptInvite(token, userEmail);
            return ResponseEntity.ok(response);
            
        } catch (UserAlreadyMemberException e) {
            logger.warn("User tried to accept invite but is already a member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Você já faz parte deste grupo!");
                
        } catch (RuntimeException e) {
            logger.error("Error accepting invite {}: {}", token, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro ao aceitar convite: " + e.getMessage());
        }
    }
}
