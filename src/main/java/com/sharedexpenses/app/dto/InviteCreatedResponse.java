package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação de convite")
public class InviteCreatedResponse {
    
    @Schema(description = "Token único do convite", example = "abc123def456")
    private String token;
    
    @Schema(description = "Link completo para aceitar o convite", example = "https://invite.divvyup.space/abc123def456")
    private String inviteLink;
    
    @Schema(description = "Data de expiração do convite", example = "2025-08-30T19:33:20")
    private String expirationDate;
    
    @Schema(description = "Tempo até expirar", example = "2 horas")
    private String expiresIn;

    public InviteCreatedResponse() {}

    public InviteCreatedResponse(String token, String inviteLink, String expirationDate, String expiresIn) {
        this.token = token;
        this.inviteLink = inviteLink;
        this.expirationDate = expirationDate;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getInviteLink() { return inviteLink; }
    public void setInviteLink(String inviteLink) { this.inviteLink = inviteLink; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public String getExpiresIn() { return expiresIn; }
    public void setExpiresIn(String expiresIn) { this.expiresIn = expiresIn; }
}
