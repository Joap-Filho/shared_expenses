-- V11: Criar tabela de cartões e vincular despesas
-- Adiciona sistema de cartões de crédito vinculados a despesas

-- Criar tabela card
CREATE TABLE card (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    due_day INTEGER NOT NULL CHECK (due_day >= 1 AND due_day <= 31),
    owner_id BIGINT NOT NULL,
    expense_space_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_card_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_card_expense_space FOREIGN KEY (expense_space_id) REFERENCES expense_space(id) ON DELETE CASCADE
);

-- Adicionar coluna card_id na tabela expense
ALTER TABLE expense ADD COLUMN card_id BIGINT;

-- Adicionar chave estrangeira para card
ALTER TABLE expense ADD CONSTRAINT fk_expense_card FOREIGN KEY (card_id) REFERENCES card(id) ON DELETE SET NULL;

-- Criar índices para performance
CREATE INDEX idx_card_owner ON card(owner_id);
CREATE INDEX idx_card_expense_space ON card(expense_space_id);
CREATE INDEX idx_expense_card ON expense(card_id);

-- Comentários explicativos
COMMENT ON TABLE card IS 'Cartões de crédito vinculados a um espaço de despesas';
COMMENT ON COLUMN card.name IS 'Nome do cartão (ex: Cartão Nubank, Visa Gold)';
COMMENT ON COLUMN card.description IS 'Descrição opcional do cartão';
COMMENT ON COLUMN card.due_day IS 'Dia de vencimento da fatura (1-31)';
COMMENT ON COLUMN card.owner_id IS 'Usuário proprietário do cartão';
COMMENT ON COLUMN card.expense_space_id IS 'Espaço de despesas ao qual o cartão pertence';
COMMENT ON COLUMN expense.card_id IS 'Cartão vinculado à despesa (opcional)';
