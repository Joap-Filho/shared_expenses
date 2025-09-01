package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.Expense;
import com.sharedexpenses.app.entity.enums.ExpenseStatus;
import com.sharedexpenses.app.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ExpenseStatusService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseStatusService.class);

    @Autowired
    private ExpenseRepository expenseRepository;

    /**
     * Executa diariamente às 6:00 AM para marcar despesas em atraso
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void markOverdueExpenses() {
        logger.info("Verificando despesas em atraso...");
        
        LocalDate today = LocalDate.now();
        
        // Buscar despesas PENDING que já passaram da data de vencimento (considerando 7 dias de graça)
        List<Expense> overdueExpenses = expenseRepository.findAll()
            .stream()
            .filter(expense -> expense.getStatus() == ExpenseStatus.PENDING)
            .filter(expense -> expense.getDate().plusDays(7).isBefore(today))
            .toList();

        int updatedCount = 0;
        for (Expense expense : overdueExpenses) {
            expense.setStatus(ExpenseStatus.OVERDUE);
            expenseRepository.save(expense);
            updatedCount++;
            
            logger.debug("Despesa marcada como OVERDUE: {} - vencimento: {}", 
                expense.getTitle(), expense.getDate());
        }

        if (updatedCount > 0) {
            logger.info("Marcadas {} despesas como OVERDUE", updatedCount);
        } else {
            logger.debug("Nenhuma despesa em atraso encontrada");
        }
    }
}
