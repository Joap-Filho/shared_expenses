-- V13: Correção de estrutura da tabela card
-- Garante que a tabela card tenha a estrutura correta

-- Verificar se a tabela card existe e criar se não existir
CREATE TABLE IF NOT EXISTS card (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    due_day INTEGER NOT NULL CHECK (due_day >= 1 AND due_day <= 31),
    owner_id BIGINT NOT NULL,
    expense_space_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Adicionar colunas que podem estar faltando
ALTER TABLE card ADD COLUMN IF NOT EXISTS closing_day INTEGER NOT NULL DEFAULT 25 CHECK (closing_day >= 1 AND closing_day <= 31);

-- Adicionar campos de fatura na expense se não existirem
ALTER TABLE expense ADD COLUMN IF NOT EXISTS card_id BIGINT;
ALTER TABLE expense ADD COLUMN IF NOT EXISTS bill_due_date DATE;
ALTER TABLE expense ADD COLUMN IF NOT EXISTS bill_period VARCHAR(7);

-- Adicionar constraints se não existirem (PostgreSQL vai ignorar se já existirem)
DO $$ BEGIN
    ALTER TABLE card ADD CONSTRAINT fk_card_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE;
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    ALTER TABLE card ADD CONSTRAINT fk_card_expense_space FOREIGN KEY (expense_space_id) REFERENCES expense_space(id) ON DELETE CASCADE;
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    ALTER TABLE expense ADD CONSTRAINT fk_expense_card FOREIGN KEY (card_id) REFERENCES card(id) ON DELETE SET NULL;
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

-- Criar índices se não existirem
CREATE INDEX IF NOT EXISTS idx_card_owner ON card(owner_id);
CREATE INDEX IF NOT EXISTS idx_card_expense_space ON card(expense_space_id);
CREATE INDEX IF NOT EXISTS idx_expense_card ON expense(card_id);
CREATE INDEX IF NOT EXISTS idx_expense_bill_period ON expense(bill_period);
CREATE INDEX IF NOT EXISTS idx_expense_card_bill_period ON expense(card_id, bill_period);
CREATE INDEX IF NOT EXISTS idx_expense_bill_due_date ON expense(bill_due_date);
