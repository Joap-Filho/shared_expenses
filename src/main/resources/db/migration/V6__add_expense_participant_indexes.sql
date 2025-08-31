-- Índices para otimização da tabela expense_participant

-- Índice composto para busca por usuário e grupo (consulta mais comum)
CREATE INDEX idx_expense_participant_user_space ON expense_participant(user_id, expense_space_id);

-- Índice para buscar todos os participantes de um grupo
CREATE INDEX idx_expense_participant_space ON expense_participant(expense_space_id);

-- Índice para buscar todos os grupos de um usuário
CREATE INDEX idx_expense_participant_user ON expense_participant(user_id);

-- Constraint única para evitar duplicação de participação
-- Comentado por enquanto para não quebrar dados existentes
-- ALTER TABLE expense_participant 
-- ADD CONSTRAINT uk_expense_participant_user_space 
-- UNIQUE (user_id, expense_space_id);
