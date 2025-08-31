package com.sharedexpenses.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.sharedexpenses.app.dto.CreateExpenseRequest;
import com.sharedexpenses.app.dto.ExpenseResponse;
import com.sharedexpenses.app.entity.Expense;
import com.sharedexpenses.app.service.ExpenseService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Gestão de Despesas", description = "Endpoints para gerenciar despesas compartilhadas")
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/create")
    @Operation(summary = "Criar despesa", description = "Registra uma nova despesa no grupo")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        Expense expense = expenseService.createExpense(request, userEmail);
        ExpenseResponse response = expenseService.convertToResponse(expense);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/space/{expenseSpaceId}")
    @Operation(summary = "Listar despesas do grupo", description = "Obtém todas as despesas de um grupo específico")
    public ResponseEntity<List<ExpenseResponse>> getExpensesBySpace(@PathVariable Long expenseSpaceId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        List<ExpenseResponse> expenses = expenseService.getExpensesBySpace(expenseSpaceId, userEmail);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{expenseId}")
    @Operation(summary = "Obter despesa por ID", description = "Obtém detalhes de uma despesa específica")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long expenseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        ExpenseResponse expense = expenseService.getExpenseById(expenseId, userEmail);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{expenseId}")
    @Operation(summary = "Atualizar despesa", description = "Atualiza uma despesa existente")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody CreateExpenseRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        Expense expense = expenseService.updateExpense(expenseId, request, userEmail);
        ExpenseResponse response = expenseService.convertToResponse(expense);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Excluir despesa", description = "Remove uma despesa do sistema")
    public ResponseEntity<String> deleteExpense(@PathVariable Long expenseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        expenseService.deleteExpense(expenseId, userEmail);
        return ResponseEntity.ok("Despesa removida com sucesso");
    }
}
