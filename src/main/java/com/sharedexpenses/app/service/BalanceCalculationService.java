package com.sharedexpenses.app.service;

import com.sharedexpenses.app.dto.UserBalanceResponse;
import com.sharedexpenses.app.dto.UserBalanceResponse.BalanceDetails;
import com.sharedexpenses.app.dto.UserBalanceResponse.BalanceDetails.ExpenseBreakdown;
import com.sharedexpenses.app.entity.Expense;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.repository.ExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BalanceCalculationService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Calcula os saldos líquidos entre todos os usuários de um espaço de despesas
     */
    public List<UserBalanceResponse> calculateSpaceBalances(Long expenseSpaceId, String userEmail) {
        // Verificar se usuário é participante do grupo
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, expenseSpaceId)) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }

        // Buscar todas as despesas do espaço
        List<Expense> expenses = expenseRepository.findByExpenseSpaceId(expenseSpaceId);

        if (expenses.isEmpty()) {
            return new ArrayList<>();
        }

        // Obter todos os usuários únicos do espaço
        Set<User> allUsers = getAllUsersFromExpenses(expenses);

        // Calcular matriz de dívidas entre todos os usuários
        Map<UserPair, DebtData> debtMatrix = calculateDebtMatrix(expenses, allUsers);

        // Compensar dívidas mútuas e criar respostas
        return compensateAndCreateResponses(debtMatrix);
    }

    /**
     * Obtém todos os usuários únicos envolvidos nas despesas
     */
    private Set<User> getAllUsersFromExpenses(List<Expense> expenses) {
        Set<User> users = new HashSet<>();
        
        for (Expense expense : expenses) {
            users.add(expense.getPaidBy()); // Quem pagou
            users.addAll(expense.getBeneficiaries()); // Todos os beneficiários
        }
        
        return users;
    }

    /**
     * Calcula matriz de dívidas entre todos os pares de usuários
     */
    private Map<UserPair, DebtData> calculateDebtMatrix(List<Expense> expenses, Set<User> allUsers) {
        Map<UserPair, DebtData> debtMatrix = new HashMap<>();

        // Inicializar matriz com zeros
        for (User user1 : allUsers) {
            for (User user2 : allUsers) {
                if (!user1.equals(user2)) {
                    UserPair pair = new UserPair(user1, user2);
                    debtMatrix.put(pair, new DebtData());
                }
            }
        }

        // Processar cada despesa
        for (Expense expense : expenses) {
            User payer = expense.getPaidBy();
            Set<User> beneficiaries = expense.getBeneficiaries();
            
            if (beneficiaries != null && !beneficiaries.isEmpty()) {
                // Calcular valor que cada beneficiário deve
                BigDecimal valuePerPerson = expense.getTotalValue().divide(
                    BigDecimal.valueOf(beneficiaries.size()), 
                    2, 
                    RoundingMode.HALF_UP
                );
                
                for (User beneficiary : beneficiaries) {
                    if (!payer.equals(beneficiary)) {
                        UserPair pair = new UserPair(beneficiary, payer);
                        DebtData debtData = debtMatrix.get(pair);
                        
                        if (debtData != null) {
                            debtData.addDebt(valuePerPerson);
                            debtData.addExpenseBreakdown(expense, valuePerPerson, "owes");
                        }
                    }
                }
            }
        }

        return debtMatrix;
    }

    /**
     * Compensa dívidas mútuas e cria respostas finais
     */
    private List<UserBalanceResponse> compensateAndCreateResponses(Map<UserPair, DebtData> debtMatrix) {
        List<UserBalanceResponse> responses = new ArrayList<>();
        Set<UserPair> processedPairs = new HashSet<>();

        for (Map.Entry<UserPair, DebtData> entry : debtMatrix.entrySet()) {
            UserPair pair = entry.getKey();
            UserPair reversePair = new UserPair(pair.getUser2(), pair.getUser1());

            // Evitar processar o mesmo par duas vezes
            if (processedPairs.contains(pair) || processedPairs.contains(reversePair)) {
                continue;
            }

            DebtData debt1to2 = entry.getValue(); // user1 deve para user2
            DebtData debt2to1 = debtMatrix.get(reversePair); // user2 deve para user1

            BigDecimal amount1to2 = debt1to2 != null ? debt1to2.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal amount2to1 = debt2to1 != null ? debt2to1.getTotalAmount() : BigDecimal.ZERO;

            // Calcular saldo líquido
            BigDecimal netAmount = amount1to2.subtract(amount2to1);

            // Só criar resposta se há saldo líquido diferente de zero
            if (netAmount.compareTo(BigDecimal.ZERO) != 0) {
                UserBalanceResponse response;
                
                if (netAmount.compareTo(BigDecimal.ZERO) > 0) {
                    // user1 deve para user2
                    response = createBalanceResponse(pair.getUser1(), pair.getUser2(), netAmount, debt1to2, debt2to1);
                } else {
                    // user2 deve para user1  
                    response = createBalanceResponse(pair.getUser2(), pair.getUser1(), netAmount.abs(), debt2to1, debt1to2);
                }
                
                responses.add(response);
            }

            processedPairs.add(pair);
            processedPairs.add(reversePair);
        }

        return responses.stream()
            .sorted((a, b) -> a.getFromUserName().compareTo(b.getFromUserName()))
            .collect(Collectors.toList());
    }

    /**
     * Cria resposta de saldo entre dois usuários
     */
    private UserBalanceResponse createBalanceResponse(User fromUser, User toUser, BigDecimal netAmount, 
                                                    DebtData fromDebt, DebtData toDebt) {
        
        BigDecimal totalFromOwes = fromDebt != null ? fromDebt.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal totalToOwes = toDebt != null ? toDebt.getTotalAmount() : BigDecimal.ZERO;
        
        List<ExpenseBreakdown> breakdown = new ArrayList<>();
        
        // Adicionar despesas onde fromUser deve para toUser
        if (fromDebt != null) {
            breakdown.addAll(fromDebt.getBreakdown());
        }
        
        // Adicionar despesas onde toUser deve para fromUser (como créditos)
        if (toDebt != null) {
            for (ExpenseBreakdown expense : toDebt.getBreakdown()) {
                ExpenseBreakdown creditExpense = new ExpenseBreakdown(
                    expense.getExpenseId(),
                    expense.getDescription(),
                    expense.getDate(),
                    expense.getPaidBy(),
                    expense.getAmount(),
                    "receives" // fromUser recebe de toUser
                );
                breakdown.add(creditExpense);
            }
        }
        
        BalanceDetails details = new BalanceDetails(totalFromOwes, totalToOwes, breakdown);
        
        return new UserBalanceResponse(
            fromUser.getName(),
            fromUser.getEmail(),
            toUser.getName(), 
            toUser.getEmail(),
            netAmount,
            details
        );
    }

    // Classes auxiliares
    private static class UserPair {
        private final User user1;
        private final User user2;

        public UserPair(User user1, User user2) {
            this.user1 = user1;
            this.user2 = user2;
        }

        public User getUser1() { return user1; }
        public User getUser2() { return user2; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserPair)) return false;
            UserPair userPair = (UserPair) o;
            return Objects.equals(user1, userPair.user1) && Objects.equals(user2, userPair.user2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user1, user2);
        }
    }

    private static class DebtData {
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private List<ExpenseBreakdown> breakdown = new ArrayList<>();

        public void addDebt(BigDecimal amount) {
            this.totalAmount = this.totalAmount.add(amount);
        }

        public void addExpenseBreakdown(Expense expense, BigDecimal amount, String direction) {
            ExpenseBreakdown expenseBreakdown = new ExpenseBreakdown(
                expense.getId(),
                expense.getDescription(),
                expense.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                expense.getPaidBy().getName(),
                amount,
                direction
            );
            this.breakdown.add(expenseBreakdown);
        }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public List<ExpenseBreakdown> getBreakdown() { return breakdown; }
    }
}
