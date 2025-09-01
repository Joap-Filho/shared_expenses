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
import com.sharedexpenses.app.dto.RecurringExpenseResponse;
import com.sharedexpenses.app.entity.Expense;
import com.sharedexpenses.app.service.ExpenseService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * Gera preview das despesas recorrentes futuras
     */
    @GetMapping("/recurring/{recurringId}/preview")
    public ResponseEntity<List<ExpenseResponse>> previewRecurringExpenses(
            @PathVariable Long recurringId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<ExpenseResponse> futureExpenses = expenseService.previewRecurringExpenses(recurringId, userEmail);
        return ResponseEntity.ok(futureExpenses);
    }

    /**
     * Lista todas as despesas recorrentes do espaço
     */
    @GetMapping("/recurring/space/{expenseSpaceId}")
    public ResponseEntity<List<RecurringExpenseResponse>> getRecurringExpensesBySpace(
            @PathVariable Long expenseSpaceId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<RecurringExpenseResponse> recurringExpenses = expenseService.getRecurringExpensesBySpace(expenseSpaceId, userEmail);
        return ResponseEntity.ok(recurringExpenses);
    }

    /**
     * Gera despesas do mês atual para todas as recorrentes ativas
     * (Útil para execução via job scheduler)
     */
    @PostMapping("/recurring/generate-current-month")
    public ResponseEntity<List<ExpenseResponse>> generateCurrentMonthRecurringExpenses(
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<ExpenseResponse> generatedExpenses = expenseService.generateCurrentMonthRecurringExpenses(userEmail);
        return ResponseEntity.ok(generatedExpenses);
    }

    /**
     * Deleta uma configuração de despesa recorrente e todas suas despesas
     */
    @DeleteMapping("/recurring/{recurringId}")
    public ResponseEntity<Void> deleteRecurringExpense(
            @PathVariable Long recurringId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        expenseService.deleteRecurringExpense(recurringId, userEmail);
        return ResponseEntity.noContent().build();
    }    /**
     * Debug: Mostra status de geração para cada configuração recorrente
     */
    @GetMapping("/recurring/debug/generation-status")
    public ResponseEntity<List<Map<String, Object>>> debugRecurringGenerationStatus(
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<Map<String, Object>> debugInfo = expenseService.debugRecurringGenerationStatus(userEmail);
        return ResponseEntity.ok(debugInfo);
    }
}
