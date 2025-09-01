package com.sharedexpenses.app.dto;

import com.sharedexpenses.app.entity.enums.ExpenseType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenseResponse {
    
    private Long id;
    private String title;
    private String description;
    private BigDecimal totalValue;
    private LocalDate date;
    private LocalDateTime createdAt;
    private ExpenseType type;
    private String status; // Status da despesa: PENDING, PAID, OVERDUE, CANCELLED
    private String paidByUserName;
    private String paidByUserEmail;
    private boolean includePayerInSplit;
    private Long expenseSpaceId;
    private String expenseSpaceName;
    private List<BeneficiaryInfo> beneficiaries;
    private BigDecimal valuePerPerson;
    private BigDecimal totalValuePerPerson; // Valor total que cada pessoa deve (para parceladas)
    private Integer totalParticipants;

    // Para despesas parceladas
    private Integer installments;
    private List<InstallmentInfo> installmentDetails;
    
    // Para despesas recorrentes  
    private String recurrenceType;
    private LocalDate endDate;
    private Long recurringExpenseId;

    // Cartão de crédito vinculado (opcional)
    private Long cardId;
    private String cardName;
    private String billPeriod; // Período da fatura (YYYY-MM)
    private LocalDate billDueDate; // Data de vencimento da fatura

    // Nested classes para informações detalhadas
    public static class BeneficiaryInfo {
        private Long userId;
        private String userName;
        private String userEmail;
        private BigDecimal amountOwed;

        public BeneficiaryInfo() {}

        public BeneficiaryInfo(Long userId, String userName, String userEmail, BigDecimal amountOwed) {
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.amountOwed = amountOwed;
        }

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public BigDecimal getAmountOwed() { return amountOwed; }
        public void setAmountOwed(BigDecimal amountOwed) { this.amountOwed = amountOwed; }
    }

    public static class InstallmentInfo {
        private Long id;
        private Integer number;
        private LocalDate dueDate;
        private BigDecimal value;
        private BigDecimal valuePerPerson; // Quanto cada pessoa paga desta parcela
        private boolean paid;

        public InstallmentInfo() {}

        public InstallmentInfo(Long id, Integer number, LocalDate dueDate, BigDecimal value, boolean paid) {
            this.id = id;
            this.number = number;
            this.dueDate = dueDate;
            this.value = value;
            this.paid = paid;
        }

        public InstallmentInfo(Long id, Integer number, LocalDate dueDate, BigDecimal value, BigDecimal valuePerPerson, boolean paid) {
            this.id = id;
            this.number = number;
            this.dueDate = dueDate;
            this.value = value;
            this.valuePerPerson = valuePerPerson;
            this.paid = paid;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Integer getNumber() { return number; }
        public void setNumber(Integer number) { this.number = number; }

        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }

        public boolean isPaid() { return paid; }
        public void setPaid(boolean paid) { this.paid = paid; }

        public BigDecimal getValuePerPerson() { return valuePerPerson; }
        public void setValuePerPerson(BigDecimal valuePerPerson) { this.valuePerPerson = valuePerPerson; }
    }

    // Constructors
    public ExpenseResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ExpenseType getType() { return type; }
    public void setType(ExpenseType type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaidByUserName() { return paidByUserName; }
    public void setPaidByUserName(String paidByUserName) { this.paidByUserName = paidByUserName; }

    public String getPaidByUserEmail() { return paidByUserEmail; }
    public void setPaidByUserEmail(String paidByUserEmail) { this.paidByUserEmail = paidByUserEmail; }

    public boolean isIncludePayerInSplit() { return includePayerInSplit; }
    public void setIncludePayerInSplit(boolean includePayerInSplit) { this.includePayerInSplit = includePayerInSplit; }

    public Long getExpenseSpaceId() { return expenseSpaceId; }
    public void setExpenseSpaceId(Long expenseSpaceId) { this.expenseSpaceId = expenseSpaceId; }

    public String getExpenseSpaceName() { return expenseSpaceName; }
    public void setExpenseSpaceName(String expenseSpaceName) { this.expenseSpaceName = expenseSpaceName; }

    public List<BeneficiaryInfo> getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(List<BeneficiaryInfo> beneficiaries) { this.beneficiaries = beneficiaries; }

    public BigDecimal getValuePerPerson() { return valuePerPerson; }
    public void setValuePerPerson(BigDecimal valuePerPerson) { this.valuePerPerson = valuePerPerson; }

    public BigDecimal getTotalValuePerPerson() { return totalValuePerPerson; }
    public void setTotalValuePerPerson(BigDecimal totalValuePerPerson) { this.totalValuePerPerson = totalValuePerPerson; }

    public Integer getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; }

    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }

    public List<InstallmentInfo> getInstallmentDetails() { return installmentDetails; }
    public void setInstallmentDetails(List<InstallmentInfo> installmentDetails) { this.installmentDetails = installmentDetails; }

    public String getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(String recurrenceType) { this.recurrenceType = recurrenceType; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getRecurringExpenseId() { return recurringExpenseId; }
    public void setRecurringExpenseId(Long recurringExpenseId) { this.recurringExpenseId = recurringExpenseId; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getBillPeriod() { return billPeriod; }
    public void setBillPeriod(String billPeriod) { this.billPeriod = billPeriod; }

    public LocalDate getBillDueDate() { return billDueDate; }
    public void setBillDueDate(LocalDate billDueDate) { this.billDueDate = billDueDate; }
}
