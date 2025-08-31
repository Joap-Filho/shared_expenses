package com.sharedexpenses.app.controller;

import com.sharedexpenses.app.dto.InviteInfoResponse;
import com.sharedexpenses.app.dto.PendingRequestResponse;
import com.sharedexpenses.app.service.InviteService;
import com.sharedexpenses.app.service.InviteOrchestrationService;
import com.sharedexpenses.app.util.AuthenticationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

@RestController
@RequestMapping("/api/invite-link")
@Tag(name = "Convites via Link", description = "Sistema de convites via link com aprovação")
public class InviteLinkController {

    private static final Logger logger = LoggerFactory.getLogger(InviteLinkController.class);

    @Autowired
    private InviteService inviteService;

    @Autowired
    private InviteOrchestrationService inviteOrchestrationService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @GetMapping("/{token}")
    @Operation(summary = "Obter informações do convite", 
               description = "Endpoint público para validar um token de convite e obter informações do grupo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações do convite obtidas com sucesso"),
        @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    public ResponseEntity<InviteInfoResponse> getInviteInfo(
            @Parameter(description = "Token do convite") @PathVariable String token) {
        
        logger.info("Processando solicitação de informações do convite para token: {}", token);
        
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitação enviada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Token inválido ou usuário já é membro"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<String> requestToJoin(
            @Parameter(description = "Token do convite") @PathVariable String token) {
        
        logger.info("Processando solicitação de entrada para token: {}", token);
        
        try {
            String userEmail = authenticationUtil.getCurrentUserEmail();
            String message = inviteOrchestrationService.requestToJoin(token, userEmail);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            logger.error("Erro ao processar solicitação de entrada: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending/{expenseSpaceId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Listar solicitações pendentes", 
               description = "Lista todas as solicitações de entrada pendentes para um grupo (apenas OWNER/ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitações pendentes obtida com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - usuário sem permissão"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<List<PendingRequestResponse>> getPendingRequests(
            @Parameter(description = "ID do espaço de despesas") @PathVariable Long expenseSpaceId) {
        
        logger.info("Listando solicitações pendentes para expense space: {}", expenseSpaceId);
        
        try {
            String userEmail = authenticationUtil.getCurrentUserEmail();
            List<PendingRequestResponse> pendingRequests = inviteOrchestrationService.getPendingRequests(expenseSpaceId, userEmail);
            return ResponseEntity.ok(pendingRequests);
        } catch (RuntimeException e) {
            logger.error("Erro ao listar solicitações pendentes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/approve/{inviteId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Aprovar solicitação", 
               description = "Aprova uma solicitação de entrada e adiciona o usuário ao grupo (apenas OWNER/ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitação aprovada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - usuário sem permissão"),
        @ApiResponse(responseCode = "400", description = "Erro na validação da solicitação"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<String> approveRequest(
            @Parameter(description = "ID do convite") @PathVariable Long inviteId) {
        
        logger.info("Aprovando solicitação de convite: {}", inviteId);
        
        try {
            String approverEmail = authenticationUtil.getCurrentUserEmail();
            String message = inviteOrchestrationService.approveRequest(inviteId, approverEmail);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            logger.error("Erro ao aprovar solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject/{inviteId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Rejeitar solicitação", 
               description = "Rejeita uma solicitação de entrada no grupo (apenas OWNER/ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitação rejeitada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - usuário sem permissão"),
        @ApiResponse(responseCode = "400", description = "Erro na validação da solicitação"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<String> rejectRequest(
            @Parameter(description = "ID do convite") @PathVariable Long inviteId) {
        
        logger.info("Rejeitando solicitação de convite: {}", inviteId);
        
        try {
            String rejecterEmail = authenticationUtil.getCurrentUserEmail();
            String message = inviteOrchestrationService.rejectRequest(inviteId, rejecterEmail);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            logger.error("Erro ao rejeitar solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
