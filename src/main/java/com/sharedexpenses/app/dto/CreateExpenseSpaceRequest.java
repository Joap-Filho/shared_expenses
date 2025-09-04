package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de um espaço de despesas")
public class CreateExpenseSpaceRequest {

    @Schema(description = "Nome do grupo/espaço de despesas", example = "República dos Amigos")
    private String name;

    @Schema(description = "Descrição do grupo/espaço de despesas", example = "Grupo para compartilhar as despesas da república")
    private String description;

    // Construtores
    public CreateExpenseSpaceRequest() {}

    public CreateExpenseSpaceRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
