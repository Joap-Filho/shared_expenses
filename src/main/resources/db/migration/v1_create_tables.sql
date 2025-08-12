-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Expense spaces (groups)
CREATE TABLE expense_space (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_by_user_id BIGINT NOT NULL REFERENCES users(id)
);

-- Roles enum
CREATE TYPE role_type AS ENUM ('OWNER', 'ADMIN', 'MEMBER');

-- Expense participants
CREATE TABLE expense_participant (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expense_space_id BIGINT NOT NULL REFERENCES expense_space(id),
    role role_type NOT NULL,
    joined_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Expense types enum
CREATE TYPE expense_type AS ENUM ('SIMPLE', 'INSTALLMENT', 'RECURRING');

-- Expenses table
CREATE TABLE expense (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    total_value NUMERIC(12,2) NOT NULL,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type expense_type NOT NULL,
    paid_by_user_id BIGINT NOT NULL REFERENCES users(id),
    include_payer_in_split BOOLEAN NOT NULL DEFAULT TRUE,
    expense_space_id BIGINT NOT NULL REFERENCES expense_space(id)
);

CREATE INDEX idx_expense_date ON expense(date);

-- Expense beneficiaries (many-to-many)
CREATE TABLE expense_beneficiary (
    expense_id BIGINT NOT NULL REFERENCES expense(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (expense_id, user_id)
);

-- Expense installments (payments by parts)
CREATE TABLE expense_installment (
    id BIGSERIAL PRIMARY KEY,
    expense_id BIGINT NOT NULL REFERENCES expense(id) ON DELETE CASCADE,
    number INT NOT NULL,
    due_date DATE NOT NULL,
    value NUMERIC(12,2) NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE
);

-- Recurrence types enum
CREATE TYPE recurrence_type AS ENUM ('MONTHLY', 'YEARLY', 'WEEKLY', 'DAILY');

-- Recurring expenses
CREATE TABLE recurring_expense (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    value NUMERIC(12,2) NOT NULL,
    recurrence recurrence_type NOT NULL,
    start_date DATE NOT NULL DEFAULT CURRENT_DATE,
    end_date DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_by_user_id BIGINT NOT NULL REFERENCES users(id),
    include_payer_in_split BOOLEAN NOT NULL DEFAULT TRUE,
    expense_space_id BIGINT NOT NULL REFERENCES expense_space(id)
);
