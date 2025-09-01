-- V10: Adicionar campo status às despesas
-- Adiciona controle de status de pagamento para despesas simples e recorrentes

-- Adicionar coluna status na tabela expense
ALTER TABLE expense ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Comentário explicativo
COMMENT ON COLUMN expense.status IS 'Status da despesa: PENDING, PAID, OVERDUE, CANCELLED';
