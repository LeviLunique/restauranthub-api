-- =====================================================
-- Migration: V5__add_phase_2_modules.sql
-- Description: Adds user type catalog, restaurants and menu items
-- =====================================================

CREATE TABLE IF NOT EXISTS tipo_usuario_cadastro (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tipo_usuario_cadastro (nome, descricao, ativo)
VALUES
    ('ADMIN', 'Administrador do sistema', TRUE),
    ('DONO_RESTAURANTE', 'Usuário responsável por restaurantes', TRUE),
    ('CLIENTE', 'Usuário consumidor da plataforma', TRUE),
    ('FUNCIONARIO', 'Usuário operacional do restaurante', TRUE)
ON CONFLICT (nome) DO NOTHING;

ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS tipo_usuario_cadastro_id BIGINT;

UPDATE usuario u
SET tipo_usuario_cadastro_id = tuc.id
FROM tipo_usuario_cadastro tuc
WHERE tuc.nome = u.tipo_usuario
  AND u.tipo_usuario_cadastro_id IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_usuario_tipo_usuario_cadastro'
    ) THEN
        ALTER TABLE usuario
            ADD CONSTRAINT fk_usuario_tipo_usuario_cadastro
            FOREIGN KEY (tipo_usuario_cadastro_id)
            REFERENCES tipo_usuario_cadastro(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_usuario_tipo_usuario_cadastro_id ON usuario(tipo_usuario_cadastro_id);

CREATE TABLE IF NOT EXISTS restaurante (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    tipo_cozinha VARCHAR(80) NOT NULL,
    horario_funcionamento VARCHAR(120) NOT NULL,
    dono_usuario_id BIGINT NOT NULL,
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2),
    cep VARCHAR(10) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurante_dono FOREIGN KEY (dono_usuario_id)
        REFERENCES usuario(id),
    CONSTRAINT chk_restaurante_estado CHECK (estado ~* '^[A-Z]{2}$' OR estado IS NULL),
    CONSTRAINT chk_restaurante_cep CHECK (cep ~* '^\d{5}-?\d{3}$')
);

CREATE INDEX IF NOT EXISTS idx_restaurante_dono_usuario_id ON restaurante(dono_usuario_id);
CREATE INDEX IF NOT EXISTS idx_restaurante_tipo_cozinha ON restaurante(tipo_cozinha);

CREATE TABLE IF NOT EXISTS item_cardapio (
    id BIGSERIAL PRIMARY KEY,
    restaurante_id BIGINT NOT NULL,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    preco NUMERIC(10,2) NOT NULL,
    apenas_no_local BOOLEAN NOT NULL DEFAULT FALSE,
    caminho_foto VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_cardapio_restaurante FOREIGN KEY (restaurante_id)
        REFERENCES restaurante(id) ON DELETE CASCADE,
    CONSTRAINT chk_item_cardapio_preco CHECK (preco >= 0)
);

CREATE INDEX IF NOT EXISTS idx_item_cardapio_restaurante_id ON item_cardapio(restaurante_id);

CREATE TRIGGER trigger_update_data_atualizacao_tipo_usuario_cadastro
    BEFORE UPDATE ON tipo_usuario_cadastro
    FOR EACH ROW
    EXECUTE FUNCTION update_data_atualizacao();

CREATE TRIGGER trigger_update_data_atualizacao_restaurante
    BEFORE UPDATE ON restaurante
    FOR EACH ROW
    EXECUTE FUNCTION update_data_atualizacao();

CREATE TRIGGER trigger_update_data_atualizacao_item_cardapio
    BEFORE UPDATE ON item_cardapio
    FOR EACH ROW
    EXECUTE FUNCTION update_data_atualizacao();

INSERT INTO restaurante (
    nome, tipo_cozinha, horario_funcionamento, dono_usuario_id, rua, numero, bairro, cidade, estado, cep, ativo
) VALUES (
    'Cantina do Roberto',
    'Italiana',
    'Seg-Sáb 11:00-23:00',
    (SELECT id FROM usuario WHERE login = 'roberto.faria'),
    'Rua Augusta',
    '1500',
    'Consolação',
    'São Paulo',
    'SP',
    '01304-001',
    TRUE
), (
    'Bistrô da Márcia',
    'Contemporânea',
    'Ter-Dom 12:00-22:00',
    (SELECT id FROM usuario WHERE login = 'marcia.oliveira'),
    'Av. Brigadeiro Faria Lima',
    '3477',
    'Itaim Bibi',
    'São Paulo',
    'SP',
    '04538-133',
    TRUE
)
ON CONFLICT DO NOTHING;

INSERT INTO item_cardapio (
    restaurante_id, nome, descricao, preco, apenas_no_local, caminho_foto, ativo
) VALUES (
    (SELECT id FROM restaurante WHERE nome = 'Cantina do Roberto'),
    'Lasanha da Casa',
    'Lasanha artesanal com molho de tomate e queijo gratinado.',
    49.90,
    FALSE,
    '/images/lasanha-da-casa.jpg',
    TRUE
), (
    (SELECT id FROM restaurante WHERE nome = 'Cantina do Roberto'),
    'Tiramisù Tradicional',
    'Sobremesa italiana clássica servida gelada.',
    22.50,
    TRUE,
    '/images/tiramisu.jpg',
    TRUE
), (
    (SELECT id FROM restaurante WHERE nome = 'Bistrô da Márcia'),
    'Risoto de Cogumelos',
    'Risoto cremoso com mix de cogumelos frescos.',
    58.00,
    FALSE,
    '/images/risoto-cogumelos.jpg',
    TRUE
)
ON CONFLICT DO NOTHING;
