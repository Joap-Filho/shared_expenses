package com.sharedexpenses.app.dto;

import java.util.List;

public class GroupBalancesResponse {
    private Long expenseSpaceId;
    private String expenseSpaceName;
    private List<UserBalanceResponse> balances;
    private BalanceSummary summary;
    
    // Construtores
    public GroupBalancesResponse() {}
    
    public GroupBalancesResponse(Long expenseSpaceId, String expenseSpaceName, 
                               List<UserBalanceResponse> balances, BalanceSummary summary) {
        this.expenseSpaceId = expenseSpaceId;
        this.expenseSpaceName = expenseSpaceName;
        this.balances = balances;
        this.summary = summary;
    }
    
    // Getters e Setters
    public Long getExpenseSpaceId() { return expenseSpaceId; }
    public void setExpenseSpaceId(Long expenseSpaceId) { this.expenseSpaceId = expenseSpaceId; }
    
    public String getExpenseSpaceName() { return expenseSpaceName; }
    public void setExpenseSpaceName(String expenseSpaceName) { this.expenseSpaceName = expenseSpaceName; }
    
    public List<UserBalanceResponse> getBalances() { return balances; }
    public void setBalances(List<UserBalanceResponse> balances) { this.balances = balances; }
    
    public BalanceSummary getSummary() { return summary; }
    public void setSummary(BalanceSummary summary) { this.summary = summary; }
    
    // Classe interna para resumo dos saldos
    public static class BalanceSummary {
        private int totalUsers;
        private int activeDebts;
        private String message;
        
        // Construtores
        public BalanceSummary() {}
        
        public BalanceSummary(int totalUsers, int activeDebts, String message) {
            this.totalUsers = totalUsers;
            this.activeDebts = activeDebts;
            this.message = message;
        }
        
        // Getters e Setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        
        public int getActiveDebts() { return activeDebts; }
        public void setActiveDebts(int activeDebts) { this.activeDebts = activeDebts; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
