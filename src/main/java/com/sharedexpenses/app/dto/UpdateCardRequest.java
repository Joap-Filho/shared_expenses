package com.sharedexpenses.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Request para atualizar um cartão existente")
public class UpdateCardRequest {
    
    @Schema(description = "Nome do cartão", example = "Cartão Nubank Roxinho Premium")
    @Size(max = 100, message = "Nome do cartão deve ter no máximo 100 caracteres")
    private String name;
    
    @Schema(description = "Descrição opcional do cartão", example = "Cartão de crédito principal para gastos mensais e viagens")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
    
    @Schema(description = "Dia de vencimento da fatura (1-31)", example = "15")
    @Min(value = 1, message = "Dia de vencimento deve ser entre 1 e 31")
    @Max(value = 31, message = "Dia de vencimento deve ser entre 1 e 31")
    private Integer dueDay;
    
    // Constructors
    public UpdateCardRequest() {}
    
    public UpdateCardRequest(String name, String description, Integer dueDay) {
        this.name = name;
        this.description = description;
        this.dueDay = dueDay;
    }
    
    // Getters and Setters
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
}
