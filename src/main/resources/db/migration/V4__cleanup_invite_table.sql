-- Limpar inconsistências na tabela de convites

-- Criar ENUM para status do convite
CREATE TYPE invite_status AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED');

-- Remover campo redundante 'used' e ajustar status
ALTER TABLE expense_space_invite 
DROP COLUMN IF EXISTS used,
ALTER COLUMN status TYPE invite_status USING status::invite_status;

-- Adicionar constraint para garantir consistência
ALTER TABLE expense_space_invite 
ADD CONSTRAINT chk_request_fields 
CHECK (
    (status = 'PENDING' AND requested_by_user_id IS NULL) OR
    (status != 'PENDING' AND requested_by_user_id IS NOT NULL)
);

-- Adicionar constraint para approval fields
ALTER TABLE expense_space_invite 
ADD CONSTRAINT chk_approval_fields 
CHECK (
    (status IN ('ACCEPTED', 'REJECTED') AND approved_rejected_by_user_id IS NOT NULL AND approved_rejected_at IS NOT NULL) OR
    (status NOT IN ('ACCEPTED', 'REJECTED'))
);

-- Criar índices para performance
CREATE INDEX idx_invite_status ON expense_space_invite(status);
CREATE INDEX idx_invite_expiration ON expense_space_invite(expiration_date);
CREATE INDEX idx_invite_space ON expense_space_invite(expense_space_id);
