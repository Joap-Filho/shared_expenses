package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para aceitação de convite")
public class InviteAcceptedResponse {
    
    @Schema(description = "Mensagem de confirmação", example = "Convite aceito com sucesso! Você agora faz parte do grupo.")
    private String message;
    
    @Schema(description = "Nome do espaço de despesas", example = "Viagem para Paris")
    private String expenseSpaceName;
    
    @Schema(description = "Papel do usuário no grupo", example = "MEMBER")
    private String role;

    public InviteAcceptedResponse() {}

    public InviteAcceptedResponse(String message, String expenseSpaceName, String role) {
        this.message = message;
        this.expenseSpaceName = expenseSpaceName;
        this.role = role;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getExpenseSpaceName() { return expenseSpaceName; }
    public void setExpenseSpaceName(String expenseSpaceName) { this.expenseSpaceName = expenseSpaceName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
