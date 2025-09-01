package com.sharedexpenses.app.entity.enums;

public enum ExpenseStatus {
    PENDING,    // Pendente - despesa criada mas ainda não paga
    PAID,       // Pago - despesa foi quitada
    OVERDUE,    // Em atraso - passou da data e não foi paga
    CANCELLED   // Cancelada - despesa foi cancelada
}
