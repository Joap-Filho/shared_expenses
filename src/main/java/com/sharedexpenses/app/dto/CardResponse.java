package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta com dados de um cartão")
public class CardResponse {
    
    @Schema(description = "ID único do cartão", example = "1")
    private Long id;
    
    @Schema(description = "Nome do cartão", example = "Cartão Nubank Roxinho")
    private String name;
    
    @Schema(description = "Descrição do cartão", example = "Cartão de crédito principal para gastos mensais")
    private String description;
    
    @Schema(description = "Dia de vencimento da fatura", example = "10")
    private Integer dueDay;
    
    @Schema(description = "ID do proprietário do cartão", example = "2")
    private Long ownerId;
    
    @Schema(description = "Nome do proprietário do cartão", example = "João Silva")
    private String ownerName;
    
    @Schema(description = "ID do espaço de despesas", example = "5")
    private Long expenseSpaceId;
    
    @Schema(description = "Nome do espaço de despesas", example = "Casa da Família")
    private String expenseSpaceName;
    
    @Schema(description = "Data de criação do cartão")
    private LocalDateTime createdAt;
    
    @Schema(description = "Data da última atualização")
    private LocalDateTime updatedAt;
    
    // Constructors
    public CardResponse() {}
    
    public CardResponse(Long id, String name, String description, Integer dueDay, 
                       Long ownerId, String ownerName, Long expenseSpaceId, 
                       String expenseSpaceName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDay = dueDay;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.expenseSpaceId = expenseSpaceId;
        this.expenseSpaceName = expenseSpaceName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getDueDay() {
        return dueDay;
    }
    
    public void setDueDay(Integer dueDay) {
        this.dueDay = dueDay;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public Long getExpenseSpaceId() {
        return expenseSpaceId;
    }
    
    public void setExpenseSpaceId(Long expenseSpaceId) {
        this.expenseSpaceId = expenseSpaceId;
    }
    
    public String getExpenseSpaceName() {
        return expenseSpaceName;
    }
    
    public void setExpenseSpaceName(String expenseSpaceName) {
        this.expenseSpaceName = expenseSpaceName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
