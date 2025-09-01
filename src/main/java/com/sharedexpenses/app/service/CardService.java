package com.sharedexpenses.app.service;

import com.sharedexpenses.app.dto.CardResponse;
import com.sharedexpenses.app.dto.CreateCardRequest;
import com.sharedexpenses.app.dto.UpdateCardRequest;
import com.sharedexpenses.app.entity.Card;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.repository.CardRepository;
import com.sharedexpenses.app.repository.ExpenseSpaceRepository;
import com.sharedexpenses.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardService {
    
    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private ExpenseSpaceRepository expenseSpaceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    /**
     * Criar um novo cartão
     */
    public CardResponse createCard(Long expenseSpaceId, String userEmail, CreateCardRequest request) {
        // Validar se o usuário é participante do espaço
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, expenseSpaceId)) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }
        
        // Buscar entidades necessárias
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        ExpenseSpace expenseSpace = expenseSpaceRepository.findById(expenseSpaceId)
                .orElseThrow(() -> new RuntimeException("Espaço de despesas não encontrado"));
        
        // Validar se já existe cartão com o mesmo nome no espaço
        if (cardRepository.existsByNameAndExpenseSpaceId(request.getName(), expenseSpaceId)) {
            throw new RuntimeException("Já existe um cartão com este nome neste espaço");
        }
        
        // Criar novo cartão
        Card card = new Card();
        card.setName(request.getName());
        card.setDescription(request.getDescription());
        card.setDueDay(request.getDueDay());
        card.setOwner(user);
        card.setExpenseSpace(expenseSpace);
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        
        Card savedCard = cardRepository.save(card);
        return convertToResponse(savedCard);
    }
    
    /**
     * Listar todos os cartões de um espaço
     */
    @Transactional(readOnly = true)
    public List<CardResponse> getCardsByExpenseSpace(Long expenseSpaceId, String userEmail) {
        // Validar se o usuário é participante do espaço
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, expenseSpaceId)) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }
        
        List<Card> cards = cardRepository.findByExpenseSpaceId(expenseSpaceId);
        return cards.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar cartões de um usuário específico em um espaço
     */
    @Transactional(readOnly = true)
    public List<CardResponse> getUserCardsByExpenseSpace(Long expenseSpaceId, String userEmail) {
        // Validar se o usuário é participante do espaço
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, expenseSpaceId)) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        List<Card> cards = cardRepository.findByOwnerIdAndExpenseSpaceId(user.getId(), expenseSpaceId);
        return cards.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar cartão por ID
     */
    @Transactional(readOnly = true)
    public CardResponse getCardById(Long cardId, String userEmail) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        
        // Validar se o usuário é participante do espaço
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, card.getExpenseSpace().getId())) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }
        
        return convertToResponse(card);
    }
    
    /**
     * Atualizar cartão
     */
    public CardResponse updateCard(Long cardId, String userEmail, UpdateCardRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        
        // Validar se o usuário é o proprietário do cartão
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!card.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Apenas o proprietário pode editar este cartão");
        }
        
        // Validar se o novo nome não conflita com outro cartão (se foi alterado)
        if (request.getName() != null && !request.getName().equals(card.getName())) {
            if (cardRepository.existsByNameAndExpenseSpaceId(request.getName(), card.getExpenseSpace().getId())) {
                throw new RuntimeException("Já existe um cartão com este nome neste espaço");
            }
            card.setName(request.getName());
        }
        
        // Atualizar campos se fornecidos
        if (request.getDescription() != null) {
            card.setDescription(request.getDescription());
        }
        if (request.getDueDay() != null) {
            card.setDueDay(request.getDueDay());
        }
        
        card.setUpdatedAt(LocalDateTime.now());
        Card updatedCard = cardRepository.save(card);
        
        return convertToResponse(updatedCard);
    }
    
    /**
     * Deletar cartão
     */
    public void deleteCard(Long cardId, String userEmail) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        
        // Validar se o usuário é o proprietário do cartão
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!card.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Apenas o proprietário pode deletar este cartão");
        }
        
        cardRepository.delete(card);
    }
    
    /**
     * Converter entidade para DTO de resposta
     */
    private CardResponse convertToResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getDescription(),
                card.getDueDay(),
                card.getOwner().getId(),
                card.getOwner().getName(),
                card.getExpenseSpace().getId(),
                card.getExpenseSpace().getName(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }
}
