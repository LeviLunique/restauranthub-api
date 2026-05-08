# Restaurant User API

API REST para gestão de usuários de restaurantes, tipos de usuário, cadastro de restaurantes, itens de cardápio, autenticação com JWT e documentação OpenAPI.

## Apresentação
- Aluno: Levi Lunique Izidio da Silva
- Professor: Vinícius G. Mendonça
- Disciplina: Desenvolvimento de Backend
- Curso: Pós-graduação em Desenvolvimento de Aplicativos Móveis
- Universidade: PUCPR — Pontifícia Universidade Católica do Paraná
- [Vídeo de apresentação no YouTube](https://youtu.be/2aEY3cYsA84)

## Stack e requisitos
- Java 25, Maven 3.9+
- Spring Boot 3.5 (Web, Security, Data JPA, Validation, Actuator)
- PostgreSQL + Flyway
- Docker / Docker Compose (execução recomendada)
- Swagger UI em `/swagger-ui.html`

## Como executar
### Via Docker Compose (recomendado)
```bash
cp .env.example .env   # se existir; caso contrário, use os defaults
docker-compose up -d
```
Aplicação em `http://localhost:8080`.

### Local com Maven
1) Suba um PostgreSQL local ou via Docker:
```bash
docker run -d --name postgres-restaurant \
  -e POSTGRES_DB=restauranthubdb \
  -e POSTGRES_USER=restauranthub \
  -e POSTGRES_PASSWORD=restaurantpwd \
  -p 5432:5432 postgres:16-alpine
```
2) Exporte o JDK 25 e execute:
```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 25)"
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw spring-boot:run
```

### Variáveis de ambiente principais
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `SERVER_PORT` (default 8080)
- `JWT_SECRET` (Base64, 256 bits) e `JWT_EXPIRATION_MS` (default 3600000)

## Endpoints principais (base `/api/v1`)
- `POST /auth/login` – autentica e retorna JWT.
- `POST /users` – cria usuário.
- `GET /users/{id}` – consulta por id.
- `GET /users` – lista paginada (`page`, `size`, `sort`).
- `GET /users/search?nome=` – busca por nome.
- `GET /users/email/{email}` – busca por e-mail.
- `PUT /users/{id}` – atualiza usuário.
- `PATCH /users/{id}/password` – altera senha.
- `DELETE /users/{id}` – desativa usuário.
- `POST /user-types` – cria tipo de usuário.
- `GET /user-types` – lista tipos de usuário.
- `PUT /user-types/{typeId}/users/{userId}` – associa um tipo a um usuário existente.
- `POST /restaurants` – cria restaurante.
- `GET /restaurants` – lista restaurantes.
- `PUT /restaurants/{id}` – atualiza restaurante.
- `POST /restaurants/{restaurantId}/menu-items` – cria item de cardápio.
- `GET /restaurants/{restaurantId}/menu-items` – lista itens do cardápio por restaurante.
- `PUT /menu-items/{id}` – atualiza item de cardápio.
- `DELETE /menu-items/{id}` – remove item de cardápio.
- Health: `/actuator/health`
- Swagger: `/swagger-ui.html`

## Modelagem e banco de dados
- Modelagem das entidades, relacionamento `1:N` entre usuário e endereço e estrutura das tabelas: [docs/modelagem-dados.md](docs/modelagem-dados.md)
- Migrations Flyway com schema, índices, constraints e seeds: [src/main/resources/db/migration/MIGRATIONS.md](src/main/resources/db/migration/MIGRATIONS.md)
- Script inicial do banco: [src/main/resources/db/migration/V1__create_initial_schema.sql](src/main/resources/db/migration/V1__create_initial_schema.sql)
- Expansão da fase 2 com catálogo de tipos de usuário, restaurantes e itens de cardápio: [src/main/resources/db/migration/V5__add_phase_2_modules.sql](src/main/resources/db/migration/V5__add_phase_2_modules.sql)

## Swagger
- A documentação em `/swagger-ui.html` agora inclui exemplos de requisição e de respostas de sucesso e erro nos endpoints principais.
- Os exemplos cobrem cenários como validação de payload, regra de negócio (`Email já cadastrado`) e autenticação.

### Fluxo de autenticação
1) `POST /auth/login` com email/senha.
2) Use o token retornado em `Authorization: Bearer <token>` nas demais rotas.

### Usuários seed (Flyway V2/V4)
- Admin: `admin@restauranthub.com` / `Admin@123`
- Dono: `roberto.faria@restaurante.com` / `Dono@123` (idem para os demais donos)
- Cliente: `joao.silva@email.com` / `Cliente@123` (idem demais clientes)
Usuários podem ser desativados, então valide `ativo=true` se reutilizar seeds.

### Dados seed da fase 2 (Flyway V5)
- Tipos de usuário cadastrados: `ADMIN`, `DONO_RESTAURANTE`, `CLIENTE`, `FUNCIONARIO`.
- Restaurantes seed:
  - `Cantina do Roberto`
  - `Bistrô da Márcia`
- Itens seed:
  - `Lasanha da Casa`
  - `Tiramisù Tradicional`
  - `Risoto de Cogumelos`

## Postman
Coleção pronta em `postman/restaurant-user-api.postman_collection.json`. Importe e:
1) Rode “Auth > Login (Admin)” para popular `{{token}}`.
2) Exercite os requests de usuários, tipos de usuário, restaurantes e cardápio (já configurados com bearer token).

### Testes automatizados via Newman (Docker)
Há um script que roda a coleção pelo Newman em container:
```bash
# Com o docker compose rodando (recomendado):
docker-compose up -d
./scripts/run-postman.sh
```
- Se o container `restauranthub-app` estiver ativo, o script descobre a rede do Compose e usa `http://restauranthub-app:8080/api/v1` automaticamente.
- Para rodar contra outro host/porta, defina `BASE_URL`, ex.: `BASE_URL="http://localhost:8080/api/v1" ./scripts/run-postman.sh`.
- Em Apple Silicon, para eliminar o aviso de plataforma, use `NEWMAN_PLATFORM=linux/arm64/v8 ./scripts/run-postman.sh`.
- O login salva o token no ambiente; os demais requests usam o Bearer automaticamente.

## Testes e qualidade
```bash
./mvnw test          # executa unit/integration/api slices (H2)
./mvnw clean test jacoco:report  # gera cobertura em target/site/jacoco/index.html
```
Os slices JPA/Controller usam H2 e desabilitam Flyway nas suites específicas; o build completo usa Flyway e PostgreSQL conforme as configs padrão.

## Troubleshooting
- **Unsupported class version**: garanta `java --version` = 25 e `mvn --version` mostrando o mesmo JDK.
- **Flyway em testes H2**: já desabilitado em `UsuarioRepositoryTest`; mantenha as props de teste se criar novos slices.
- **JWT inválido nos testes locais**: use um `JWT_SECRET` Base64 de 32 bytes (ex.: `MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=`).***
