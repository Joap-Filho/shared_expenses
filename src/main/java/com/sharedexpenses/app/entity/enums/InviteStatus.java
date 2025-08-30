package com.sharedexpenses.app.entity.enums;

public enum InviteStatus {
    PENDING,    // Aguardando interação do convidado
    REQUESTED,  // Convidado fez login/registro, aguarda aprovação
    ACCEPTED,   // Aprovado e usuário adicionado
    REJECTED,   // Rejeitado pelo admin
    EXPIRED     // Expirado
}
