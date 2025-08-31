package com.sharedexpenses.app.dto;

import com.sharedexpenses.app.entity.enums.ExpenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateExpenseRequest {
    
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    
    @NotNull(message = "Valor total é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal totalValue;
    
    // Data é opcional - se não informada, usa data atual
    private LocalDate date;
    
    @NotNull(message = "Tipo de despesa é obrigatório")
    private ExpenseType type;
    
    @NotNull(message = "ID do espaço de despesas é obrigatório")
    private Long expenseSpaceId;
    
    private boolean includePayerInSplit = true;
    
    // Para despesas que não incluem todos os participantes automaticamente
    private List<Long> beneficiaryIds;
    
    // Para despesas parceladas
    private Integer installments;
    
    // Para despesas recorrentes
    private String recurrenceType; // MONTHLY, YEARLY, WEEKLY, DAILY
    private LocalDate endDate;

    // Constructors
    public CreateExpenseRequest() {}

    public CreateExpenseRequest(String description, BigDecimal totalValue, LocalDate date, 
                              ExpenseType type, Long expenseSpaceId) {
        this.description = description;
        this.totalValue = totalValue;
        this.date = date;
        this.type = type;
        this.expenseSpaceId = expenseSpaceId;
    }

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public ExpenseType getType() { return type; }
    public void setType(ExpenseType type) { this.type = type; }

    public Long getExpenseSpaceId() { return expenseSpaceId; }
    public void setExpenseSpaceId(Long expenseSpaceId) { this.expenseSpaceId = expenseSpaceId; }

    public boolean isIncludePayerInSplit() { return includePayerInSplit; }
    public void setIncludePayerInSplit(boolean includePayerInSplit) { this.includePayerInSplit = includePayerInSplit; }

    public List<Long> getBeneficiaryIds() { return beneficiaryIds; }
    public void setBeneficiaryIds(List<Long> beneficiaryIds) { this.beneficiaryIds = beneficiaryIds; }

    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }

    public String getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(String recurrenceType) { this.recurrenceType = recurrenceType; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
