package com.sharedexpenses.app.dto;

import java.math.BigDecimal;
import java.util.List;

public class UserBalanceResponse {
    
    private String fromUserName;
    private String fromUserEmail;
    private String toUserName;
    private String toUserEmail;
    private BigDecimal netAmount;
    private BalanceDetails details;
    
    public static class BalanceDetails {
        private BigDecimal totalFromOwes;
        private BigDecimal totalToOwes;
        private List<ExpenseBreakdown> breakdown;
        
        public static class ExpenseBreakdown {
            private Long expenseId;
            private String description;
            private String date;
            private String paidBy;
            private BigDecimal amount;
            private String direction; // "owes" ou "receives"
            
            public ExpenseBreakdown() {}
            
            public ExpenseBreakdown(Long expenseId, String description, String date, 
                                   String paidBy, BigDecimal amount, String direction) {
                this.expenseId = expenseId;
                this.description = description;
                this.date = date;
                this.paidBy = paidBy;
                this.amount = amount;
                this.direction = direction;
            }
            
            // Getters and Setters
            public Long getExpenseId() { return expenseId; }
            public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
            
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
            
            public String getDate() { return date; }
            public void setDate(String date) { this.date = date; }
            
            public String getPaidBy() { return paidBy; }
            public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
            
            public BigDecimal getAmount() { return amount; }
            public void setAmount(BigDecimal amount) { this.amount = amount; }
            
            public String getDirection() { return direction; }
            public void setDirection(String direction) { this.direction = direction; }
        }
        
        public BalanceDetails() {}
        
        public BalanceDetails(BigDecimal totalFromOwes, BigDecimal totalToOwes, List<ExpenseBreakdown> breakdown) {
            this.totalFromOwes = totalFromOwes;
            this.totalToOwes = totalToOwes;
            this.breakdown = breakdown;
        }
        
        // Getters and Setters
        public BigDecimal getTotalFromOwes() { return totalFromOwes; }
        public void setTotalFromOwes(BigDecimal totalFromOwes) { this.totalFromOwes = totalFromOwes; }
        
        public BigDecimal getTotalToOwes() { return totalToOwes; }
        public void setTotalToOwes(BigDecimal totalToOwes) { this.totalToOwes = totalToOwes; }
        
        public List<ExpenseBreakdown> getBreakdown() { return breakdown; }
        public void setBreakdown(List<ExpenseBreakdown> breakdown) { this.breakdown = breakdown; }
    }
    
    public UserBalanceResponse() {}
    
    public UserBalanceResponse(String fromUserName, String fromUserEmail, 
                             String toUserName, String toUserEmail, 
                             BigDecimal netAmount, BalanceDetails details) {
        this.fromUserName = fromUserName;
        this.fromUserEmail = fromUserEmail;
        this.toUserName = toUserName;
        this.toUserEmail = toUserEmail;
        this.netAmount = netAmount;
        this.details = details;
    }
    
    // Getters and Setters
    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }
    
    public String getFromUserEmail() { return fromUserEmail; }
    public void setFromUserEmail(String fromUserEmail) { this.fromUserEmail = fromUserEmail; }
    
    public String getToUserName() { return toUserName; }
    public void setToUserName(String toUserName) { this.toUserName = toUserName; }
    
    public String getToUserEmail() { return toUserEmail; }
    public void setToUserEmail(String toUserEmail) { this.toUserEmail = toUserEmail; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public BalanceDetails getDetails() { return details; }
    public void setDetails(BalanceDetails details) { this.details = details; }
}
