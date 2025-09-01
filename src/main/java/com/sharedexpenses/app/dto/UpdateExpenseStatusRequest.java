package com.sharedexpenses.app.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateExpenseStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private String status; // PENDING, PAID, OVERDUE, CANCELLED

    public UpdateExpenseStatusRequest() {}

    public UpdateExpenseStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
