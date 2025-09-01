package com.sharedexpenses.app.controller;

import com.sharedexpenses.app.dto.UserBalanceResponse;
import com.sharedexpenses.app.service.BalanceCalculationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Balance Calculation", description = "Endpoints para cálculo de saldos entre usuários")
@SecurityRequirement(name = "Bearer Authentication")
public class BalanceController {

    @Autowired
    private BalanceCalculationService balanceCalculationService;

    @GetMapping("/balances/space/{expenseSpaceId}")
    @Operation(summary = "Calcular saldos do grupo", 
               description = "Calcula os saldos líquidos entre todos os usuários do grupo com compensação automática de dívidas mútuas")
    public ResponseEntity<List<UserBalanceResponse>> calculateSpaceBalances(
            @PathVariable Long expenseSpaceId,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        List<UserBalanceResponse> balances = balanceCalculationService.calculateSpaceBalances(expenseSpaceId, userEmail);
        
        return ResponseEntity.ok(balances);
    }
}
