package com.sharedexpenses.app.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateInstallmentStatusRequest {

    @NotNull(message = "Status de pagamento é obrigatório")
    private Boolean paid;

    public UpdateInstallmentStatusRequest() {}

    public UpdateInstallmentStatusRequest(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }
}
