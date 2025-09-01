-- V12: Adicionar lógica funcional de cartões de crédito
-- Adiciona campos para cálculo de fatura e período de fechamento

-- Adicionar campo closing_day na tabela card
ALTER TABLE card ADD COLUMN closing_day INTEGER NOT NULL DEFAULT 25 CHECK (closing_day >= 1 AND closing_day <= 31);

-- Adicionar campos de fatura na tabela expense
ALTER TABLE expense ADD COLUMN bill_due_date DATE;
ALTER TABLE expense ADD COLUMN bill_period VARCHAR(7); -- Formato YYYY-MM

-- Criar índices para consultas por fatura
CREATE INDEX idx_expense_bill_period ON expense(bill_period);
CREATE INDEX idx_expense_card_bill_period ON expense(card_id, bill_period);
CREATE INDEX idx_expense_bill_due_date ON expense(bill_due_date);

-- Comentários explicativos
COMMENT ON COLUMN card.closing_day IS 'Dia de fechamento da fatura (1-31). Despesas após esse dia entram na próxima fatura';
COMMENT ON COLUMN expense.bill_due_date IS 'Data de vencimento da fatura quando despesa vinculada a cartão';
COMMENT ON COLUMN expense.bill_period IS 'Período da fatura no formato YYYY-MM (ex: 2025-09)';
