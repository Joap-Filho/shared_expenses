-- Adiciona referência para recurring_expense na tabela expense
-- Isso permitirá relacionar diretamente uma despesa com sua configuração de recorrência

ALTER TABLE expense
ADD COLUMN recurring_expense_id BIGINT;

-- Adicionar foreign key constraint
ALTER TABLE expense
ADD CONSTRAINT fk_expense_recurring_expense
FOREIGN KEY (recurring_expense_id) REFERENCES recurring_expense(id);

-- Criar índice para melhorar performance nas consultas
CREATE INDEX idx_expense_recurring_expense_id ON expense(recurring_expense_id);
