-- Adiciona campos para o novo sistema de convites via link
ALTER TABLE expense_space_invite 
ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING',
ADD COLUMN requested_by_user_id BIGINT REFERENCES users(id),
ADD COLUMN requested_at TIMESTAMP WITHOUT TIME ZONE,
ADD COLUMN approved_rejected_at TIMESTAMP WITHOUT TIME ZONE,
ADD COLUMN approved_rejected_by_user_id BIGINT REFERENCES users(id);

-- Atualizar convites existentes
UPDATE expense_space_invite 
SET status = CASE 
    WHEN used = true THEN 'ACCEPTED'
    WHEN expiration_date < CURRENT_TIMESTAMP THEN 'EXPIRED'
    ELSE 'PENDING'
END;
