package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com dados de um espaço de despesas")
public class ExpenseSpaceResponse {

    @Schema(description = "ID do espaço de despesas")
    private Long id;

    @Schema(description = "Nome do grupo/espaço de despesas")
    private String name;

    @Schema(description = "Descrição do grupo/espaço de despesas")
    private String description;

    @Schema(description = "Nome do usuário que criou o espaço")
    private String createdByUserName;

    @Schema(description = "Email do usuário que criou o espaço")
    private String createdByUserEmail;

    @Schema(description = "Papel do usuário atual no grupo")
    private String userRole;

    @Schema(description = "Número de participantes do grupo")
    private Integer totalParticipants;

    // Construtores
    public ExpenseSpaceResponse() {}

    public ExpenseSpaceResponse(Long id, String name, String description, String createdByUserName, 
                               String createdByUserEmail, String userRole, Integer totalParticipants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdByUserName = createdByUserName;
        this.createdByUserEmail = createdByUserEmail;
        this.userRole = userRole;
        this.totalParticipants = totalParticipants;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedByUserName() { return createdByUserName; }
    public void setCreatedByUserName(String createdByUserName) { this.createdByUserName = createdByUserName; }

    public String getCreatedByUserEmail() { return createdByUserEmail; }
    public void setCreatedByUserEmail(String createdByUserEmail) { this.createdByUserEmail = createdByUserEmail; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public Integer getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; }
}
