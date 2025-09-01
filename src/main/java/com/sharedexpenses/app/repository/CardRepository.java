package com.sharedexpenses.app.repository;

import com.sharedexpenses.app.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    /**
     * Buscar todos os cartões de um espaço de despesas
     */
    List<Card> findByExpenseSpaceId(Long expenseSpaceId);
    
    /**
     * Buscar cartões de um proprietário específico em um espaço
     */
    List<Card> findByOwnerIdAndExpenseSpaceId(Long ownerId, Long expenseSpaceId);
    
    /**
     * Buscar cartão por ID e espaço (para validar acesso)
     */
    Optional<Card> findByIdAndExpenseSpaceId(Long cardId, Long expenseSpaceId);
    
    /**
     * Verificar se existe cartão com o mesmo nome no espaço
     */
    boolean existsByNameAndExpenseSpaceId(String name, Long expenseSpaceId);
    
    /**
     * Contar quantos cartões um usuário possui no espaço
     */
    @Query("SELECT COUNT(c) FROM Card c WHERE c.owner.id = :ownerId AND c.expenseSpace.id = :expenseSpaceId")
    long countByOwnerIdAndExpenseSpaceId(@Param("ownerId") Long ownerId, @Param("expenseSpaceId") Long expenseSpaceId);
    
    /**
     * Buscar cartões com vencimento em um dia específico
     */
    @Query("SELECT c FROM Card c WHERE c.dueDay = :dueDay AND c.expenseSpace.id = :expenseSpaceId")
    List<Card> findByDueDayAndExpenseSpaceId(@Param("dueDay") Integer dueDay, @Param("expenseSpaceId") Long expenseSpaceId);
}
