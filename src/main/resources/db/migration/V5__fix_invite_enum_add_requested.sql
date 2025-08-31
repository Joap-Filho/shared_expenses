-- Adicionar valor REQUESTED ao enum invite_status
ALTER TYPE invite_status ADD VALUE 'REQUESTED';

-- Não é necessário fazer mais nada, pois a V4 anterior já:
-- 1. Converteu status para ENUM
-- 2. Removeu o campo 'used' 
-- 3. Adicionou as constraints
-- 4. Criou os índices

-- Esta migration apenas complementa adicionando o valor que estava faltando
