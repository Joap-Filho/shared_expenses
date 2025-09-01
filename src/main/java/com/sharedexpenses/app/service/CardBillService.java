package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.Card;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
public class CardBillService {
    
    /**
     * Calcula o período da fatura (YYYY-MM) baseado na data da despesa e dia de fechamento do cartão
     * 
     * @param expenseDate Data da despesa
     * @param card Cartão de crédito
     * @return Período da fatura no formato "YYYY-MM"
     */
    public String calculateBillPeriod(LocalDate expenseDate, Card card) {
        if (card == null) {
            return null;
        }
        
        int closingDay = card.getClosingDay();
        YearMonth expenseYearMonth = YearMonth.from(expenseDate);
        
        // Se a despesa foi feita antes do fechamento, entra na fatura do mês atual
        if (expenseDate.getDayOfMonth() <= closingDay) {
            return expenseYearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        
        // Se a despesa foi feita depois do fechamento, entra na fatura do próximo mês
        YearMonth nextMonth = expenseYearMonth.plusMonths(1);
        return nextMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
    
    /**
     * Calcula a data de vencimento da fatura baseada no período da fatura e dia de vencimento do cartão
     * 
     * @param billPeriod Período da fatura (YYYY-MM)
     * @param card Cartão de crédito
     * @return Data de vencimento da fatura
     */
    public LocalDate calculateBillDueDate(String billPeriod, Card card) {
        if (billPeriod == null || card == null) {
            return null;
        }
        
        // Parse do período da fatura
        YearMonth billYearMonth = YearMonth.parse(billPeriod, DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // Vencimento é no mês seguinte ao da fatura, no dia especificado no cartão
        YearMonth dueYearMonth = billYearMonth.plusMonths(1);
        
        // Ajusta o dia caso o mês não tenha o dia especificado (ex: 31 em fevereiro)
        int dueDay = Math.min(card.getDueDay(), dueYearMonth.lengthOfMonth());
        
        return dueYearMonth.atDay(dueDay);
    }
    
    /**
     * Calcula tanto o período quanto a data de vencimento de uma vez
     * 
     * @param expenseDate Data da despesa
     * @param card Cartão de crédito
     * @return Array com [billPeriod, billDueDate] ou null se cartão for null
     */
    public Object[] calculateBillInfo(LocalDate expenseDate, Card card) {
        if (card == null) {
            return null;
        }
        
        String billPeriod = calculateBillPeriod(expenseDate, card);
        LocalDate billDueDate = calculateBillDueDate(billPeriod, card);
        
        return new Object[]{billPeriod, billDueDate};
    }
    
    /**
     * Verifica se uma data está no período de uma fatura específica
     * 
     * @param date Data a verificar
     * @param billPeriod Período da fatura (YYYY-MM)
     * @param card Cartão de crédito
     * @return true se a data pertence ao período da fatura
     */
    public boolean isDateInBillPeriod(LocalDate date, String billPeriod, Card card) {
        if (card == null || billPeriod == null) {
            return false;
        }
        
        String dateBillPeriod = calculateBillPeriod(date, card);
        return billPeriod.equals(dateBillPeriod);
    }
}
