-- =====================================================
-- Migration: V1__create_initial_schema.sql
-- Description: Creates the initial database schema
-- Author: Levi Lunique
-- =====================================================

-- Create table: USUARIO
CREATE TABLE IF NOT EXISTS usuario (
    id                  BIGSERIAL PRIMARY KEY,
    nome                VARCHAR(100) NOT NULL,
    email               VARCHAR(100) NOT NULL UNIQUE,
    login               VARCHAR(50) NOT NULL UNIQUE,
    senha               VARCHAR(255) NOT NULL,
    tipo_usuario        VARCHAR(20) NOT NULL,
    telefone            VARCHAR(20),
    data_criacao        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Constraints
    CONSTRAINT chk_tipo_usuario CHECK (tipo_usuario IN ('DONO_RESTAURANTE', 'CLIENTE', 'FUNCIONARIO', 'ADMIN')),
    CONSTRAINT chk_email_formato CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_login_formato CHECK (login ~* '^[a-zA-Z0-9._-]{3,50}$')
);

-- Create table: ENDERECO
CREATE TABLE IF NOT EXISTS endereco (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL,
    tipo_endereco       VARCHAR(20) NOT NULL DEFAULT 'RESIDENCIAL',
    rua                 VARCHAR(255) NOT NULL,
    numero              VARCHAR(20),
    complemento         VARCHAR(100),
    bairro              VARCHAR(100),
    cidade              VARCHAR(100) NOT NULL,
    estado              VARCHAR(2),
    cep                 VARCHAR(10) NOT NULL,
    principal           BOOLEAN NOT NULL DEFAULT TRUE,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key
    CONSTRAINT fk_endereco_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuario(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_tipo_endereco CHECK (tipo_endereco IN ('RESIDENCIAL', 'COMERCIAL', 'COBRANCA', 'ENTREGA')),
    CONSTRAINT chk_cep_formato CHECK (cep ~* '^\d{5}-?\d{3}$'),
    CONSTRAINT chk_estado_uf CHECK (estado ~* '^[A-Z]{2}$' OR estado IS NULL)
);

-- Create indexes for better query performance on USUARIO
CREATE INDEX idx_email ON usuario(email);
CREATE INDEX idx_login ON usuario(login);
CREATE INDEX idx_tipo_usuario ON usuario(tipo_usuario);
CREATE INDEX idx_ativo ON usuario(ativo);
CREATE INDEX idx_data_criacao ON usuario(data_criacao);

-- Create indexes for better query performance on ENDERECO
CREATE INDEX idx_endereco_usuario_id ON endereco(usuario_id);
CREATE INDEX idx_endereco_cep ON endereco(cep);
CREATE INDEX idx_endereco_cidade ON endereco(cidade);
CREATE INDEX idx_endereco_estado ON endereco(estado);
CREATE INDEX idx_endereco_principal ON endereco(principal);
CREATE INDEX idx_endereco_tipo ON endereco(tipo_endereco);
CREATE INDEX idx_endereco_ativo ON endereco(ativo);

-- Create function to update data_atualizacao automatically
CREATE OR REPLACE FUNCTION update_data_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_atualizacao = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to update data_atualizacao on UPDATE for USUARIO
CREATE TRIGGER trigger_update_data_atualizacao_usuario
    BEFORE UPDATE ON usuario
    FOR EACH ROW
    EXECUTE FUNCTION update_data_atualizacao();

-- Create trigger to update data_atualizacao on UPDATE for ENDERECO
CREATE TRIGGER trigger_update_data_atualizacao_endereco
    BEFORE UPDATE ON endereco
    FOR EACH ROW
    EXECUTE FUNCTION update_data_atualizacao();

-- Add comments to USUARIO table and columns
COMMENT ON TABLE usuario IS 'Tabela de usuários do sistema RestaurantHub';
COMMENT ON COLUMN usuario.id IS 'Identificador único do usuário';
COMMENT ON COLUMN usuario.nome IS 'Nome completo do usuário (obrigatório)';
COMMENT ON COLUMN usuario.email IS 'Email único do usuário (obrigatório)';
COMMENT ON COLUMN usuario.login IS 'Login único do usuário para autenticação (obrigatório)';
COMMENT ON COLUMN usuario.senha IS 'Senha criptografada com BCrypt (obrigatório)';
COMMENT ON COLUMN usuario.tipo_usuario IS 'Tipo de usuário: DONO_RESTAURANTE, CLIENTE, FUNCIONARIO ou ADMIN';
COMMENT ON COLUMN usuario.telefone IS 'Telefone de contato do usuário';
COMMENT ON COLUMN usuario.data_criacao IS 'Data e hora de criação do registro';
COMMENT ON COLUMN usuario.data_atualizacao IS 'Data e hora da última atualização (obrigatório)';
COMMENT ON COLUMN usuario.ativo IS 'Indica se o usuário está ativo no sistema';

-- Add comments to ENDERECO table and columns
COMMENT ON TABLE endereco IS 'Tabela de endereços dos usuários (relacionamento 1:N)';
COMMENT ON COLUMN endereco.id IS 'Identificador único do endereço';
COMMENT ON COLUMN endereco.usuario_id IS 'Referência ao usuário proprietário do endereço';
COMMENT ON COLUMN endereco.tipo_endereco IS 'Tipo: RESIDENCIAL, COMERCIAL, COBRANCA ou ENTREGA';
COMMENT ON COLUMN endereco.rua IS 'Nome da rua/logradouro (obrigatório)';
COMMENT ON COLUMN endereco.numero IS 'Número do endereço';
COMMENT ON COLUMN endereco.complemento IS 'Complemento (apto, bloco, sala, etc)';
COMMENT ON COLUMN endereco.bairro IS 'Bairro/distrito';
COMMENT ON COLUMN endereco.cidade IS 'Cidade (obrigatório)';
COMMENT ON COLUMN endereco.estado IS 'Estado/UF (sigla de 2 letras)';
COMMENT ON COLUMN endereco.cep IS 'CEP no formato 00000-000 (obrigatório)';
COMMENT ON COLUMN endereco.principal IS 'Indica se é o endereço principal do usuário';
COMMENT ON COLUMN endereco.ativo IS 'Indica se o endereço está ativo';
COMMENT ON COLUMN endereco.data_criacao IS 'Data e hora de criação do endereço';
COMMENT ON COLUMN endereco.data_atualizacao IS 'Data e hora da última atualização';
