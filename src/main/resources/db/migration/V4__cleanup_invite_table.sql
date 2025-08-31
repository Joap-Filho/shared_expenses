-- Limpar inconsistências na tabela de convites

-- Criar ENUM para status do convite (incluindo REQUESTED)
CREATE TYPE invite_status AS ENUM ('PENDING', 'REQUESTED', 'ACCEPTED', 'REJECTED', 'EXPIRED');

-- Primeiro, atualizar todos os valores existentes para serem compatíveis
UPDATE expense_space_invite 
SET status = CASE 
    WHEN used = true THEN 'ACCEPTED'
    WHEN expiration_date < CURRENT_TIMESTAMP THEN 'EXPIRED'
    ELSE 'PENDING'
END;

-- Remover o valor padrão temporariamente
ALTER TABLE expense_space_invite ALTER COLUMN status DROP DEFAULT;

-- Converter a coluna para o novo tipo ENUM
ALTER TABLE expense_space_invite 
ALTER COLUMN status TYPE invite_status USING status::invite_status;

-- Definir o novo valor padrão
ALTER TABLE expense_space_invite ALTER COLUMN status SET DEFAULT 'PENDING'::invite_status;

-- Remover campo redundante 'used'
ALTER TABLE expense_space_invite DROP COLUMN IF EXISTS used;

-- Adicionar constraint para garantir consistência dos campos de solicitação
ALTER TABLE expense_space_invite 
ADD CONSTRAINT chk_request_fields 
CHECK (
    (status = 'PENDING' AND requested_by_user_id IS NULL) OR
    (status != 'PENDING' AND requested_by_user_id IS NOT NULL)
);

-- Adicionar constraint para campos de aprovação/rejeição
ALTER TABLE expense_space_invite 
ADD CONSTRAINT chk_approval_fields 
CHECK (
    (status IN ('ACCEPTED', 'REJECTED') AND approved_rejected_by_user_id IS NOT NULL AND approved_rejected_at IS NOT NULL) OR
    (status NOT IN ('ACCEPTED', 'REJECTED'))
);

-- Criar índices para performance
CREATE INDEX IF NOT EXISTS idx_invite_status ON expense_space_invite(status);
CREATE INDEX IF NOT EXISTS idx_invite_expiration ON expense_space_invite(expiration_date);
CREATE INDEX IF NOT EXISTS idx_invite_space ON expense_space_invite(expense_space_id);
