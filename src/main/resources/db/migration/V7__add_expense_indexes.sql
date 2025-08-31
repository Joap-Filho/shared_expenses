-- Índices para otimização das tabelas de despesas

-- Índices para a tabela expense
CREATE INDEX idx_expense_space_id ON expense(expense_space_id);
CREATE INDEX idx_expense_paid_by_user_id ON expense(paid_by_user_id);
CREATE INDEX idx_expense_type ON expense(type);
CREATE INDEX idx_expense_space_date ON expense(expense_space_id, date DESC);

-- Índices para a tabela expense_beneficiary
CREATE INDEX idx_expense_beneficiary_expense_id ON expense_beneficiary(expense_id);
CREATE INDEX idx_expense_beneficiary_user_id ON expense_beneficiary(user_id);

-- Índices para a tabela expense_installment
CREATE INDEX idx_expense_installment_expense_id ON expense_installment(expense_id);
CREATE INDEX idx_expense_installment_due_date ON expense_installment(due_date);
CREATE INDEX idx_expense_installment_paid ON expense_installment(paid);
CREATE INDEX idx_expense_installment_expense_number ON expense_installment(expense_id, number);

-- Índices para a tabela recurring_expense
CREATE INDEX idx_recurring_expense_space_id ON recurring_expense(expense_space_id);
CREATE INDEX idx_recurring_expense_paid_by_user_id ON recurring_expense(paid_by_user_id);
CREATE INDEX idx_recurring_expense_start_date ON recurring_expense(start_date);
CREATE INDEX idx_recurring_expense_end_date ON recurring_expense(end_date);
CREATE INDEX idx_recurring_expense_recurrence ON recurring_expense(recurrence);
