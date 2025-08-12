package com.sharedexpenses.app.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExpenseBeneficiaryId implements Serializable {

    private Long expenseId;
    private Long userId;

    public ExpenseBeneficiaryId() {}

    public ExpenseBeneficiaryId(Long expenseId, Long userId) {
        this.expenseId = expenseId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenseBeneficiaryId that)) return false;
        return Objects.equals(expenseId, that.expenseId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseId, userId);
    }
}

