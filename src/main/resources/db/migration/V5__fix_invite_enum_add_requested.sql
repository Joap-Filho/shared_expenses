-- Verificar se REQUESTED já existe no enum invite_status antes de adicionar
DO $$ 
BEGIN
    -- Tentar adicionar o valor REQUESTED apenas se ele não existir
    IF NOT EXISTS (
        SELECT 1 FROM pg_enum e
        JOIN pg_type t ON e.enumtypid = t.oid
        WHERE t.typname = 'invite_status' AND e.enumlabel = 'REQUESTED'
    ) THEN
        ALTER TYPE invite_status ADD VALUE 'REQUESTED';
    END IF;
END $$;

-- Nota: Esta migration foi criada porque inicialmente pensamos que o valor 
-- REQUESTED não existia, mas a V4 já o havia criado. Esta verificação
-- garante que a migration seja idempotente.
