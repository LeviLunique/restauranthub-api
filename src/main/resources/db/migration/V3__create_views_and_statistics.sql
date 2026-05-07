-- =====================================================
-- Migration: V3__create_views_and_statistics.sql
-- Description: Creates views for statistics and reports
-- Author: Levi Lunique
-- =====================================================

-- Create view: Active users summary
CREATE OR REPLACE VIEW v_usuarios_ativos AS
SELECT 
    tipo_usuario,
    COUNT(*) as total,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM usuario WHERE ativo = true), 2) as percentual
FROM usuario
WHERE ativo = true
GROUP BY tipo_usuario
ORDER BY tipo_usuario;

-- Create view: User statistics
CREATE OR REPLACE VIEW v_estatisticas_usuarios AS
SELECT 
    COUNT(*) as total_usuarios,
    COUNT(CASE WHEN ativo = true THEN 1 END) as usuarios_ativos,
    COUNT(CASE WHEN ativo = false THEN 1 END) as usuarios_inativos,
    COUNT(CASE WHEN tipo_usuario = 'ADMIN' THEN 1 END) as total_admins,
    COUNT(CASE WHEN tipo_usuario = 'FUNCIONARIO' THEN 1 END) as total_funcionarios,
    COUNT(CASE WHEN tipo_usuario = 'CLIENTE' THEN 1 END) as total_clientes,
    COUNT(CASE WHEN data_criacao >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as novos_ultimos_7_dias,
    COUNT(CASE WHEN data_criacao >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as novos_ultimos_30_dias
FROM usuario;

-- Create view: User details with primary address (without password)
CREATE OR REPLACE VIEW v_usuarios_detalhes AS
SELECT 
    u.id,
    u.nome,
    u.email,
    u.login,
    u.tipo_usuario,
    u.telefone,
    u.ativo as usuario_ativo,
    u.data_criacao as usuario_data_criacao,
    u.data_atualizacao as usuario_data_atualizacao,
    e.id as endereco_id,
    e.tipo_endereco,
    e.rua,
    e.numero,
    e.complemento,
    e.bairro,
    e.cidade,
    e.estado,
    e.cep,
    e.principal as endereco_principal,
    e.ativo as endereco_ativo,
    EXTRACT(DAYS FROM (CURRENT_TIMESTAMP - u.data_criacao)) as dias_desde_criacao,
    EXTRACT(DAYS FROM (CURRENT_TIMESTAMP - u.data_atualizacao)) as dias_desde_ultima_atualizacao
FROM usuario u
LEFT JOIN endereco e ON u.id = e.usuario_id AND e.principal = true
ORDER BY u.data_criacao DESC;

-- Create view: Complete user addresses
CREATE OR REPLACE VIEW v_usuarios_enderecos AS
SELECT 
    u.id as usuario_id,
    u.nome,
    u.email,
    u.login,
    u.tipo_usuario,
    u.ativo as usuario_ativo,
    e.id as endereco_id,
    e.tipo_endereco,
    e.rua,
    e.numero,
    e.complemento,
    e.bairro,
    e.cidade,
    e.estado,
    e.cep,
    e.principal,
    e.ativo as endereco_ativo,
    CONCAT(e.rua, ', ', e.numero, 
           COALESCE(' - ' || e.complemento, ''), 
           ' - ', e.bairro, 
           ' - ', e.cidade, '/', e.estado, 
           ' - CEP: ', e.cep) as endereco_completo
FROM usuario u
INNER JOIN endereco e ON u.id = e.usuario_id
WHERE e.ativo = true
ORDER BY u.nome, e.principal DESC, e.data_criacao DESC;

-- Add comments to views
COMMENT ON VIEW v_usuarios_ativos IS 'Resumo de usuários ativos agrupados por tipo';
COMMENT ON VIEW v_estatisticas_usuarios IS 'Estatísticas gerais dos usuários do sistema';
COMMENT ON VIEW v_usuarios_detalhes IS 'Detalhes dos usuários com endereço principal (sem senha)';
COMMENT ON VIEW v_usuarios_enderecos IS 'Relação completa de usuários e todos seus endereços ativos';
