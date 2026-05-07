# Database Migrations

Este diretório contém os scripts de migração do banco de dados usando Flyway.

## 📋 Estrutura das Migrations

### V1__create_initial_schema.sql
**Descrição**: Cria o schema inicial do banco de dados com normalização de endereços

**Estrutura**:

#### Tabela USUARIO
- Dados principais do usuário (nome, email, login, senha, tipo)
- **Sem campos de endereço** (normalização)
- Índices:
  - `idx_email`: Busca por email
  - `idx_login`: Autenticação
  - `idx_tipo_usuario`: Filtro por tipo
  - `idx_ativo`: Filtro por status
  - `idx_data_criacao`: Ordenação por data
- Constraints:
  - Check constraint para `tipo_usuario` (DONO_RESTAURANTE, CLIENTE, FUNCIONARIO, ADMIN)
  - Check constraint para formato de email
  - Check constraint para formato de login

#### Tabela ENDERECO (1:N com USUARIO)
- Endereços dos usuários (múltiplos por usuário)
- Campos: rua, numero, complemento, bairro, cidade, estado, cep
- Campo `tipo_endereco`: RESIDENCIAL, COMERCIAL, COBRANCA, ENTREGA
- Campo `principal`: Indica endereço principal
- Foreign Key para `usuario.id` com CASCADE DELETE
- Índices:
  - `idx_endereco_usuario_id`: JOINs eficientes
  - `idx_endereco_cep`: Busca por localização
  - `idx_endereco_cidade`: Busca geográfica
  - `idx_endereco_estado`: Filtros regionais
  - `idx_endereco_principal`: Buscar endereço principal
  - `idx_endereco_tipo`: Filtro por tipo
  - `idx_endereco_ativo`: Filtro por status
- Constraints:
  - Check constraint para `tipo_endereco`
  - Check constraint para formato de CEP
  - Check constraint para formato de UF

#### Triggers
- Trigger para atualizar `data_atualizacao` automaticamente em ambas as tabelas

#### Comentários
- Documentação completa em todas as colunas de ambas as tabelas

### V2__insert_initial_data.sql
**Descrição**: Insere dados iniciais para teste

**Dados inseridos**:
- **1 ADMIN**
  - Email: `admin@restauranthub.com`
  - Login: `admin`
  - Senha: `Admin@123`
  
- **3 DONOS DE RESTAURANTE**
  - roberto.faria@restaurante.com (Login: roberto.faria)
  - marcia.oliveira@restaurante.com (Login: marcia.oliveira)
  - fernando.santos@restaurante.com (Login: fernando.santos)
  - Senha: `Dono@123`
  
- **6 CLIENTES** (5 ativos + 1 inativo)
  - joao.silva@email.com (Login: joao.silva)
  - fernanda.costa@email.com (Login: fernanda.costa)
  - ricardo.mendes@email.com (Login: ricardo.mendes)
  - patricia.lima@email.com (Login: patricia.lima)
  - roberto.alves@email.com (Login: roberto.alves)
  - inativo@email.com (Login: inativo) - INATIVO
  - Senha: `Cliente@123`

**Total**: 10 usuários de teste

### V3__create_views_and_statistics.sql
**Descrição**: Cria views para estatísticas e relatórios com suporte a endereços

**Views criadas**:
- `v_usuarios_ativos`: Resumo de usuários ativos por tipo
- `v_estatisticas_usuarios`: Estatísticas gerais do sistema
- `v_usuarios_detalhes`: Detalhes dos usuários com endereço principal (sem senha)
- `v_usuarios_enderecos`: Relação completa de usuários e todos seus endereços ativos

### V5__add_phase_2_modules.sql
**Descrição**: Expande a solução para a fase 2 com catálogo de tipos de usuário, restaurantes e itens de cardápio

**Estrutura**:

#### Tabela TIPO_USUARIO_CADASTRO
- Catálogo persistido de tipos de usuário
- Campos: `nome`, `descricao`, `ativo`, datas
- Seeds iniciais:
  - `ADMIN`
  - `DONO_RESTAURANTE`
  - `CLIENTE`
  - `FUNCIONARIO`

#### Alteração na tabela USUARIO
- Novo campo `tipo_usuario_cadastro_id`
- Associação `N:1` entre `usuario` e `tipo_usuario_cadastro`
- Migração popula automaticamente o vínculo com base no enum `tipo_usuario`

#### Tabela RESTAURANTE
- Cadastro principal do restaurante
- Campos: `nome`, `tipo_cozinha`, `horario_funcionamento`, endereço e `dono_usuario_id`
- Regra: o dono deve ser um usuário do tipo `DONO_RESTAURANTE`

#### Tabela ITEM_CARDAPIO
- Itens vendidos pelo restaurante
- Campos: `nome`, `descricao`, `preco`, `apenas_no_local`, `caminho_foto`, `ativo`
- Relacionamento `N:1` com `restaurante`

#### Seeds da fase 2
- Restaurantes:
  - `Cantina do Roberto`
  - `Bistrô da Márcia`
- Itens:
  - `Lasanha da Casa`
  - `Tiramisù Tradicional`
  - `Risoto de Cogumelos`

## 🔐 Credenciais de Teste

### Admin
- **Email**: admin@restauranthub.com
- **Login**: admin
- **Senha**: Admin@123
- **Tipo**: ADMIN

### Donos de Restaurante
- **Senhas**: Dono@123 (todos)
- **Usuários**:
  - roberto.faria@restaurante.com (Login: roberto.faria)
  - marcia.oliveira@restaurante.com (Login: marcia.oliveira)
  - fernando.santos@restaurante.com (Login: fernando.santos)

### Clientes
- **Senhas**: Cliente@123 (todos)
- **Usuários**:
  - joao.silva@email.com (Login: joao.silva)
  - fernanda.costa@email.com (Login: fernanda.costa)
  - ricardo.mendes@email.com (Login: ricardo.mendes)
  - patricia.lima@email.com (Login: patricia.lima)
  - roberto.alves@email.com (Login: roberto.alves)

## 📊 Consultas Úteis

### Ver todos os usuários ativos com endereço principal
```sql
SELECT * FROM v_usuarios_detalhes WHERE usuario_ativo = true;
```

### Ver todos os endereços de um usuário
```sql
SELECT * FROM v_usuarios_enderecos WHERE usuario_id = 1;
```

### Ver estatísticas gerais
```sql
SELECT * FROM v_estatisticas_usuarios;
```

### Ver resumo por tipo
```sql
SELECT * FROM v_usuarios_ativos;
```

### Buscar usuários por cidade
```sql
SELECT DISTINCT u.* 
FROM usuario u
INNER JOIN endereco e ON u.id = e.usuario_id
WHERE e.cidade = 'São Paulo' AND e.ativo = true;
```

### Buscar endereços por CEP
```sql
SELECT u.nome, u.email, e.* 
FROM usuario u
INNER JOIN endereco e ON u.id = e.usuario_id
WHERE e.cep LIKE '04052%';
```

### Contar endereços por usuário
```sql
SELECT u.nome, COUNT(e.id) as total_enderecos
FROM usuario u
LEFT JOIN endereco e ON u.id = e.usuario_id AND e.ativo = true
GROUP BY u.id, u.nome
ORDER BY total_enderecos DESC;
```

## 🚀 Execução das Migrations

As migrations são executadas automaticamente pelo Flyway quando a aplicação inicia.

### Ordem de execução:
1. V1 - Cria as tabelas e estruturas
2. V2 - Insere dados iniciais
3. V3 - Cria views e estatísticas
4. V4 - Atualiza os hashes das senhas seed
5. V5 - Cria o catálogo de tipos de usuário, restaurantes e itens de cardápio

### Verificar migrations aplicadas:
```sql
SELECT version, description, success, installed_on 
FROM flyway_schema_history 
ORDER BY installed_rank;
```

## ⚠️ Notas Importantes

1. **Senhas**: Todas as senhas estão criptografadas com BCrypt
2. **Nunca edite** uma migration já aplicada - crie uma nova versão
3. **Nomenclatura**: Use o padrão `V{número}__{descrição}.sql`
4. **Ambiente de produção**: Remova ou desabilite a V2 (dados de teste)

## 🔄 Rollback

O Flyway não suporta rollback automático. Para reverter:
1. Crie uma nova migration com as alterações reversas
2. Exemplo: `V4__rollback_something.sql`

## 📚 Referências

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [BCrypt Password Encoding](https://bcrypt-generator.com/)
