package com.sharedexpenses.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecurringExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal value;
    private String recurrenceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private String paidByUserName;
    private String paidByUserEmail;
    private boolean includePayerInSplit;
    private Long expenseSpaceId;
    private String expenseSpaceName;

    // Constructors
    public RecurringExpenseResponse() {}

    public RecurringExpenseResponse(Long id, String description, BigDecimal value, String recurrenceType,
                                  LocalDate startDate, LocalDate endDate, LocalDateTime createdAt,
                                  String paidByUserName, String paidByUserEmail, boolean includePayerInSplit,
                                  Long expenseSpaceId, String expenseSpaceName) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.recurrenceType = recurrenceType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.paidByUserName = paidByUserName;
        this.paidByUserEmail = paidByUserEmail;
        this.includePayerInSplit = includePayerInSplit;
        this.expenseSpaceId = expenseSpaceId;
        this.expenseSpaceName = expenseSpaceName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(String recurrenceType) { this.recurrenceType = recurrenceType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

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
}
