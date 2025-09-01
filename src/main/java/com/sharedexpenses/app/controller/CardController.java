package com.sharedexpenses.app.controller;

import com.sharedexpenses.app.dto.CardResponse;
import com.sharedexpenses.app.dto.CreateCardRequest;
import com.sharedexpenses.app.dto.UpdateCardRequest;
import com.sharedexpenses.app.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Operações relacionadas a cartões de crédito vinculados a despesas")
public class CardController {
    
    @Autowired
    private CardService cardService;
    
    @PostMapping("/space/{expenseSpaceId}")
    @Operation(summary = "Criar um novo cartão", description = "Cria um novo cartão de crédito vinculado ao espaço de despesas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não pertence ao espaço de despesas"),
            @ApiResponse(responseCode = "409", description = "Já existe um cartão com este nome no espaço")
    })
    public ResponseEntity<CardResponse> createCard(
            @Parameter(description = "ID do espaço de despesas", required = true)
            @PathVariable Long expenseSpaceId,
            @Parameter(description = "Dados do cartão a ser criado", required = true)
            @Valid @RequestBody CreateCardRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        CardResponse response = cardService.createCard(expenseSpaceId, userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/space/{expenseSpaceId}")
    @Operation(summary = "Listar cartões do espaço", description = "Lista todos os cartões de crédito do espaço de despesas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cartões recuperada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não pertence ao espaço de despesas"),
            @ApiResponse(responseCode = "404", description = "Espaço de despesas não encontrado")
    })
    public ResponseEntity<List<CardResponse>> getCardsByExpenseSpace(
            @Parameter(description = "ID do espaço de despesas", required = true)
            @PathVariable Long expenseSpaceId,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        List<CardResponse> cards = cardService.getCardsByExpenseSpace(expenseSpaceId, userEmail);
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/space/{expenseSpaceId}/my-cards")
    @Operation(summary = "Listar meus cartões", description = "Lista apenas os cartões de crédito do usuário autenticado no espaço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cartões do usuário recuperada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não pertence ao espaço de despesas"),
            @ApiResponse(responseCode = "404", description = "Espaço de despesas não encontrado")
    })
    public ResponseEntity<List<CardResponse>> getMyCardsByExpenseSpace(
            @Parameter(description = "ID do espaço de despesas", required = true)
            @PathVariable Long expenseSpaceId,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        List<CardResponse> cards = cardService.getUserCardsByExpenseSpace(expenseSpaceId, userEmail);
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/{cardId}")
    @Operation(summary = "Buscar cartão por ID", description = "Recupera os detalhes de um cartão específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não pertence ao espaço de despesas"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado")
    })
    public ResponseEntity<CardResponse> getCardById(
            @Parameter(description = "ID do cartão", required = true)
            @PathVariable Long cardId,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        CardResponse card = cardService.getCardById(cardId, userEmail);
        return ResponseEntity.ok(card);
    }
    
    @PutMapping("/{cardId}")
    @Operation(summary = "Atualizar cartão", description = "Atualiza os dados de um cartão existente. Apenas o proprietário pode editar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Apenas o proprietário pode editar este cartão"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "409", description = "Já existe um cartão com este nome no espaço")
    })
    public ResponseEntity<CardResponse> updateCard(
            @Parameter(description = "ID do cartão", required = true)
            @PathVariable Long cardId,
            @Parameter(description = "Novos dados do cartão", required = true)
            @Valid @RequestBody UpdateCardRequest request,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        CardResponse response = cardService.updateCard(cardId, userEmail, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{cardId}")
    @Operation(summary = "Excluir cartão", description = "Remove um cartão do sistema. Apenas o proprietário pode excluir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cartão excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Apenas o proprietário pode excluir este cartão"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado")
    })
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID do cartão", required = true)
            @PathVariable Long cardId,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        cardService.deleteCard(cardId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
