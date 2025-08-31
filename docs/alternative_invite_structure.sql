-- Opcional: Separar convites de solicitações para maior clareza

-- Tabela para convites criados por admins (link sharing)
CREATE TABLE invite_link (
    id BIGSERIAL PRIMARY KEY,
    expense_space_id BIGINT NOT NULL REFERENCES expense_space(id),
    token VARCHAR(64) NOT NULL UNIQUE,
    created_by_user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabela para solicitações de entrada via link
CREATE TABLE membership_request (
    id BIGSERIAL PRIMARY KEY,
    invite_link_id BIGINT NOT NULL REFERENCES invite_link(id),
    requested_by_user_id BIGINT NOT NULL REFERENCES users(id),
    requested_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status invite_status NOT NULL DEFAULT 'PENDING',
    processed_at TIMESTAMP WITHOUT TIME ZONE,
    processed_by_user_id BIGINT REFERENCES users(id),
    UNIQUE(invite_link_id, requested_by_user_id)
);

-- Migrar dados existentes seria necessário se implementar essa abordagem
