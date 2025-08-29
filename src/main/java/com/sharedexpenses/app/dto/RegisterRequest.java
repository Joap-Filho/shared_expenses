package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para registro de novo usuário")
public class RegisterRequest {

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String name;
    
    @Schema(description = "Email do usuário (deve ser único)", example = "joao@email.com")
    private String email;
    
    @Schema(description = "Senha do usuário (mínimo 6 caracteres)", example = "123456")
    private String password;

    // Getters e Setters

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String passowrd) { this.password = passowrd; }
}
