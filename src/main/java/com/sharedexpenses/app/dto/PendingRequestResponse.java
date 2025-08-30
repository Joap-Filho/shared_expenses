package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Solicitação de entrada em grupo pendente de aprovação")
public class PendingRequestResponse {

    @Schema(description = "ID do convite")
    private Long inviteId;

    @Schema(description = "Nome do usuário solicitante")
    private String userName;

    @Schema(description = "Email do usuário solicitante")
    private String userEmail;

    @Schema(description = "Data da solicitação")
    private LocalDateTime requestedAt;

    @Schema(description = "Token do convite")
    private String token;

    // Construtores
    public PendingRequestResponse() {}

    public PendingRequestResponse(Long inviteId, String userName, String userEmail, LocalDateTime requestedAt, String token) {
        this.inviteId = inviteId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.requestedAt = requestedAt;
        this.token = token;
    }

    // Getters e Setters
    public Long getInviteId() { return inviteId; }
    public void setInviteId(Long inviteId) { this.inviteId = inviteId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
