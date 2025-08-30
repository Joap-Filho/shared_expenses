package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações do convite e do grupo")
public class InviteInfoResponse {

    @Schema(description = "Token do convite")
    private String token;

    @Schema(description = "ID do espaço de despesas")
    private Long expenseSpaceId;

    @Schema(description = "Nome do grupo")
    private String groupName;

    @Schema(description = "Nome do criador do convite")
    private String invitedBy;

    @Schema(description = "Se o convite ainda é válido")
    private boolean valid;

    @Schema(description = "Mensagem de erro se inválido")
    private String errorMessage;

    // Construtores
    public InviteInfoResponse() {}

    public InviteInfoResponse(String token, Long expenseSpaceId, String groupName, String invitedBy, boolean valid, String errorMessage) {
        this.token = token;
        this.expenseSpaceId = expenseSpaceId;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getExpenseSpaceId() { return expenseSpaceId; }
    public void setExpenseSpaceId(Long expenseSpaceId) { this.expenseSpaceId = expenseSpaceId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getInvitedBy() { return invitedBy; }
    public void setInvitedBy(String invitedBy) { this.invitedBy = invitedBy; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
