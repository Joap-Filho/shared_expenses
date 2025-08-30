package com.sharedexpenses.app.controller;

import com.sharedexpenses.app.entity.ExpenseSpaceInvite;
import com.sharedexpenses.app.entity.ExpenseParticipant;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.service.InviteService;
import com.sharedexpenses.app.service.ExpenseParticipantService;
import com.sharedexpenses.app.repository.UserRepository;
import com.sharedexpenses.app.repository.ExpenseParticipantRepository;
import com.sharedexpenses.app.repository.ExpenseSpaceRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invites")
@Tag(name = "Convites", description = "Endpoints para gerenciar convites para espaços de despesas")
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseSpaceInviteController {

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

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostMapping("/create")
    @Operation(summary = "Criar convite", description = "Gera um token de convite para adicionar novos membros ao espaço de despesas (apenas OWNER/ADMIN)")
    public ResponseEntity<?> createInvite(@RequestParam Long expenseSpaceId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        RoleType role = expenseParticipantService.getRoleByUserEmailAndExpenseSpaceId(userEmail, expenseSpaceId);
        if (role != RoleType.OWNER && role != RoleType.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        ExpenseSpaceInvite invite = inviteService.createInvite(
                expenseSpaceId,
                userRepository.findByEmail(userEmail).get().getId());

        // Criar resposta com link formatado
        Map<String, Object> response = new HashMap<>();
        response.put("invite", invite);
        response.put("inviteLink", frontendUrl + "/" + invite.getToken()); // Link direto para o token
        response.put("apiEndpoint", "/api/invite/" + invite.getToken() + "/info"); // Para debug
        response.put("expiresIn", "2 horas");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept")
    @Operation(summary = "Aceitar convite", description = "Aceita um convite através do token e adiciona o usuário ao espaço de despesas")
    public ResponseEntity<?> acceptInvite(@RequestParam String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ExpenseSpaceInvite invite = inviteService.validateInvite(token);

  User user = userRepository.findByEmail(userEmail)
    .orElseThrow(() -> new RuntimeException("User not found"));

// Buscar ExpenseSpace pelo id do convite
ExpenseSpace expenseSpace = expenseSpaceRepository.findById(invite.getExpenseSpaceId())
    .orElseThrow(() -> new RuntimeException("ExpenseSpace not found"));

// Criar participante e setar as entidades
ExpenseParticipant participant = new ExpenseParticipant();
participant.setUser(user);
participant.setExpenseSpace(expenseSpace);
participant.setRole(RoleType.MEMBER);
participant.setJoinedAt(LocalDateTime.now());

expenseParticipantRepository.save(participant);

        expenseParticipantRepository.save(participant);

        inviteService.markInviteUsed(invite);

        return ResponseEntity.ok("Invite accepted and user added to group.");
    }
}
