package com.sharedexpenses.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sharedexpenses.app.dto.CreateExpenseSpaceRequest;
import com.sharedexpenses.app.dto.ExpenseSpaceResponse;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.repository.UserRepository;
import com.sharedexpenses.app.service.ExpenseSpaceService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;


@RestController
@RequestMapping("/api/expense-spaces")
@Tag(name = "Espaços de Despesas", description = "Endpoints para gerenciar grupos de despesas")
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseSpaceController {

    @Autowired
    private ExpenseSpaceService expenseSpaceService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    @Operation(summary = "Criar espaço de despesas", description = "Cria um novo grupo para compartilhamento de despesas")
    public ResponseEntity<?> createExpenseSpace(@RequestBody CreateExpenseSpaceRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ExpenseSpace expenseSpace = expenseSpaceService.createExpenseSpace(
            request.getName(),
            request.getDescription(),
            user
        );

        return ResponseEntity.ok(expenseSpace);
    }

    @GetMapping("/my-spaces")
    @Operation(summary = "Listar espaços do usuário", description = "Lista todos os espaços de despesas dos quais o usuário faz parte")
    public ResponseEntity<List<ExpenseSpaceResponse>> getUserExpenseSpaces() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        List<ExpenseSpaceResponse> spaces = expenseSpaceService.getUserExpenseSpaces(email);
        return ResponseEntity.ok(spaces);
    }

    @GetMapping("/{spaceId}")
    @Operation(summary = "Obter detalhes do espaço", description = "Obtém detalhes completos de um espaço de despesas específico")
    public ResponseEntity<ExpenseSpaceResponse> getExpenseSpaceDetails(@PathVariable Long spaceId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        ExpenseSpaceResponse space = expenseSpaceService.getExpenseSpaceDetails(spaceId, email);
        return ResponseEntity.ok(space);
    }
}
