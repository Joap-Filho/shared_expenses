package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de um espaço de despesas")
public class CreateExpenseSpaceRequest {

    @Schema(description = "Nome do grupo/espaço de despesas", example = "República dos Amigos")
    private String name;

    // Construtores
    public CreateExpenseSpaceRequest() {}

    public CreateExpenseSpaceRequest(String name) {
        this.name = name;
    }

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
