package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.Expense;
import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.RecurringExpense;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.entity.enums.ExpenseType;
import com.sharedexpenses.app.repository.ExpenseRepository;
import com.sharedexpenses.app.repository.ExpenseParticipantRepository;
import com.sharedexpenses.app.repository.RecurringExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecurringExpenseScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RecurringExpenseScheduler.class);

    @Autowired
    private RecurringExpenseRepository recurringExpenseRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;

    /**
     * Executa todo dia 1º às 6:00 AM
     * Gera automaticamente as despesas recorrentes do mês atual
     */
    @Scheduled(cron = "0 0 6 1 * ?")
    public void generateMonthlyRecurringExpenses() {
        logger.info("Iniciando geração automática de despesas recorrentes para {}", LocalDate.now());
        
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate nextMonth = currentMonth.plusMonths(1);
        
        try {
            // Buscar todas as configurações recorrentes ativas
            List<RecurringExpense> activeRecurring = recurringExpenseRepository.findAll()
                .stream()
                .filter(r -> isRecurringActive(r, currentMonth))
                .collect(Collectors.toList());

            logger.info("Encontradas {} configurações recorrentes ativas", activeRecurring.size());

            int generatedCount = 0;
            int skippedCount = 0;

            for (RecurringExpense recurring : activeRecurring) {
                try {
                    // Verificar se já existe despesa deste mês
                    boolean alreadyExists = expenseRepository.findByExpenseSpaceId(recurring.getExpenseSpace().getId())
                        .stream()
                        .anyMatch(e -> e.getRecurringExpense() != null &&
                            e.getRecurringExpense().getId().equals(recurring.getId()) &&
                            e.getDate().isAfter(currentMonth.minusDays(1)) &&
                            e.getDate().isBefore(nextMonth));

                    if (!alreadyExists) {
                        // Criar despesa para este mês
                        Expense newExpense = createExpenseFromRecurring(recurring, currentMonth);
                        
                        // Definir beneficiários (todos os participantes do grupo)
                        Set<User> participants = getExpenseSpaceParticipants(recurring.getExpenseSpace());
                        newExpense.setBeneficiaries(participants);
                        
                        expenseRepository.save(newExpense);
                        generatedCount++;
                        
                        logger.info("Despesa recorrente gerada: {} - R$ {} para grupo '{}'", 
                            recurring.getDescription(), 
                            recurring.getValue(),
                            recurring.getExpenseSpace().getName());
                    } else {
                        skippedCount++;
                        logger.debug("Despesa já existe para este mês: {} no grupo '{}'", 
                            recurring.getDescription(),
                            recurring.getExpenseSpace().getName());
                    }
                } catch (Exception e) {
                    logger.error("Erro ao gerar despesa recorrente ID {}: {}", recurring.getId(), e.getMessage());
                }
            }

            logger.info("Geração automática concluída: {} despesas criadas, {} ignoradas (já existiam)", 
                generatedCount, skippedCount);

        } catch (Exception e) {
            logger.error("Erro durante geração automática de despesas recorrentes", e);
        }
    }

    /**
     * Para fins de teste - executa a cada minuto (descomente apenas para debug)
     */
    // @Scheduled(fixedRate = 60000) // A cada 1 minuto
    public void debugGenerateRecurring() {
        logger.debug("Debug: Executando geração de despesas recorrentes");
        generateMonthlyRecurringExpenses();
    }

    /**
     * Verifica se uma configuração recorrente está ativa para o período
     */
    private boolean isRecurringActive(RecurringExpense recurring, LocalDate currentMonth) {
        // Deve ter começado antes ou no mês atual
        boolean hasStarted = !recurring.getStartDate().isAfter(currentMonth);
        
        // Não deve ter terminado ainda
        boolean hasNotEnded = recurring.getEndDate() == null || 
            !recurring.getEndDate().isBefore(currentMonth);
        
        return hasStarted && hasNotEnded;
    }

    /**
     * Cria uma despesa baseada em uma configuração recorrente
     */
    private Expense createExpenseFromRecurring(RecurringExpense recurring, LocalDate dueDate) {
        Expense expense = new Expense();
        expense.setDescription(recurring.getDescription() + " - " + dueDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
        expense.setTotalValue(recurring.getValue());
        expense.setDate(dueDate);
        expense.setType(ExpenseType.RECURRING);
        expense.setPaidBy(recurring.getPaidBy());
        expense.setIncludePayerInSplit(recurring.isIncludePayerInSplit());
        expense.setExpenseSpace(recurring.getExpenseSpace());
        expense.setRecurringExpense(recurring);
        
        return expense;
    }

    /**
     * Obtém participantes de um expense space
     */
    private Set<User> getExpenseSpaceParticipants(ExpenseSpace expenseSpace) {
        return expenseParticipantRepository.findByExpenseSpaceId(expenseSpace.getId())
            .stream()
            .map(participant -> participant.getUser())
            .collect(Collectors.toSet());
    }
}
