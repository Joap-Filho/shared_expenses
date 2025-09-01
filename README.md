# Shared Expenses API

API para gerenciamento de despesas compartilhadas entre pessoas que convivem em um mesmo ambiente (como repúblicas ou casas divididas). Permite criação de grupos de despesas, adição de participantes via convites, registro de gastos (com parcelamento ou recorrência), divisão automática e cálculo de saldos.

## Objetivo

Simplificar o controle de despesas entre pessoas que dividem contas, automatizando o cálculo de quem deve quanto a quem, permitindo compartilhamento de registros e facilitando o controle financeiro coletivo.

## Status do Projeto

### ✅ Implementado

- **Autenticação e Usuários**
  - Cadastro de usuários (`AuthController`)
  - Login com JWT (`JwtService`)
  - Autenticação via Spring Security (`SecurityConfig`, `JwtAuthenticationFilter`)
  - UserDetailsService customizado (`CustomUserDetailsService`)

- **Gestão de Espaços de Despesas**
  - Criação de espaços de despesas (`ExpenseSpaceService`)
  - Sistema de participantes com papéis (OWNER, ADMIN, MEMBER)
  - Serviço de gerenciamento de participantes (`ExpenseParticipantService`)

- **Sistema de Convites**
  - Geração de tokens de convite (`InviteService`)
  - Validação e expiração de convites (2h para links)
  - Aceitação de convites via token
  - Endpoints para criar e aceitar convites (`ExpenseSpaceInviteController`)
  - **Sistema de aprovação via link** (`InviteLinkController`)
    - Links públicos: `https://invite.divvyup.space/{token}`
    - Solicitações de entrada com aprovação manual
    - Gestão de solicitações pendentes

- **Arquitetura e Organização**
  - Services de orquestração (`InviteOrchestrationService`)
  - Services de autorização (`AuthorizationService`)
  - Utilitários de autenticação (`AuthenticationUtil`)
  - DTOs específicos para respostas de API
  - Separação clara de responsabilidades (Clean Architecture)

- **Configuração e Segurança**
  - CORS configurado para permitir qualquer origem (`CorsConfig`)
  - Autenticação JWT em todos os endpoints protegidos
  - Configuração de segurança via Spring Security

- **Documentação API**
  - Swagger/OpenAPI 3 integrado
  - Interface interativa em `/docs`
  - Documentação completa com exemplos e schemas

- **Gestão Completa de Despesas**
  - Controller REST para CRUD de despesas (`ExpenseController`)
  - Service com lógica de negócio e cálculo automático de divisão (`ExpenseService`)
  - DTOs específicos para requests e responses (`CreateExpenseRequest`, `ExpenseResponse`)
  - Suporte a despesas simples, parceladas e recorrentes
  - Divisão automática entre beneficiários com cálculos precisos
  - Validação de permissões para criar/editar/excluir despesas
  - Sistema de beneficiários customizáveis (todos do grupo ou específicos)

- **Sistema de Saldos Dinâmicos** ⭐ **NOVO**
  - Cálculo automático de saldos líquidos entre usuários (`BalanceCalculationService`)
  - Compensação automática de dívidas mútuas sem modificar despesas originais
  - Endpoint para visualizar "quem deve quanto para quem" (`BalanceController`)
  - Breakdown detalhado mostrando como chegou ao saldo final
  - **Exemplo:** Maria deve R$100 para João, João deve R$30 para Maria → Saldo: Maria deve R$70 para João

- **Sistema de Status de Despesas** ⭐ **NOVO**
  - Controle de status: PENDING, PAID, OVERDUE, CANCELLED
  - Atualização automática para OVERDUE quando apropriado
  - Endpoints para alterar status individual de despesas e parcelas
  - Rastreamento do ciclo de vida das despesas

- **Sistema de Cartões de Crédito** ⭐ **NOVO**
  - Criação e gestão de cartões vinculados a espaços de despesas (`CardService`)
  - Cada cartão possui nome, descrição, dia de vencimento e proprietário
  - Vinculação opcional de despesas a cartões específicos
  - CRUD completo com validações de propriedade e unicidade por espaço
  - Controle de acesso: apenas proprietário pode editar/excluir seus cartões
  - Sistema totalmente retrocompatível - despesas existentes não são afetadas

- **Estrutura de Dados e Banco**
  - Entidades JPA completas (User, ExpenseSpace, ExpenseParticipant, Expense, Card, etc.)
  - Migrations com Flyway (V1 a V11: tabelas, convites, status, cartões, índices de performance)
  - Relacionamentos entre entidades configurados
  - Sistema de ENUMs para status e tipos (ExpenseType: SIMPLE, INSTALLMENT, RECURRING; ExpenseStatus: PENDING, PAID, OVERDUE, CANCELLED)
  - Índices otimizados para consultas de alta performance
  - Constraints de integridade e prevenção de duplicatas

- **Containerização e Deploy**
  - Dockerfile para aplicação Spring Boot
  - Docker Compose com PostgreSQL
  - Configuração de variáveis de ambiente

### 🚧 Em Desenvolvimento/Pendente

- **Dashboard e Relatórios**
  - Relatórios de gastos por período/categoria
  - Visualizações gráficas de despesas
  - **Relatórios por cartão** - Gastos mensais, limites, vencimentos
  - **Dashboard de cartões** - Painel com informações consolidadas por cartão

- **Funcionalidades Avançadas de Despesas**
  - Categorização de despesas (alimentação, moradia, etc.)
  - Lembretes de vencimento para parcelas
  - **Filtros por cartão** - Buscar despesas vinculadas a cartões específicos

- **Melhorias do Sistema de Cartões**
  - Notificações de vencimento de faturas
  - Sistema de limites de gastos por cartão
  - Relatórios de fatura por período

- **Melhorias do Sistema de Convites**
  - Interface web para aceitar convites
  - Links de convite mais amigáveis

## Tecnologias Utilizadas

- **Backend:** Java 21 + Spring Boot 3.5.4
- **Segurança:** Spring Security + JWT
- **Banco de Dados:** PostgreSQL 15
- **ORM:** Spring Data JPA + Hibernate
- **Migrations:** Flyway
- **Documentação API:** Swagger/OpenAPI 3
- **Containerização:** Docker + Docker Compose
- **Build:** Maven
- **Arquitetura:** Clean Architecture, SOLID principles

## 📚 Documentação da API

A aplicação inclui documentação interativa da API usando Swagger/OpenAPI 3:

- **Interface Swagger:** http://localhost:8080/docs
- **Especificação OpenAPI:** http://localhost:8080/api-docs

### Funcionalidades do Swagger:
- 🔍 **Explorar endpoints** - Visualize todos os endpoints disponíveis
- 🧪 **Testar APIs** - Execute requests diretamente da interface
- 🔐 **Autenticação JWT** - Configure o token Bearer para endpoints protegidos
- 📖 **Documentação detalhada** - Veja parâmetros, respostas e exemplos

### Como usar:
1. Acesse http://localhost:8080/docs
2. Faça o registro/login em `/auth` para obter seu JWT token
3. Clique em "Authorize" e insira o token no formato: `Bearer seu-token-aqui`
4. Teste os endpoints protegidos!

## Endpoints Disponíveis

### 📋 Documentação Interativa da API
- **Swagger UI:** `GET /docs` - Interface interativa para testar os endpoints
- **OpenAPI JSON:** `GET /api-docs` - Especificação da API em formato JSON

### Autenticação
- `POST /auth/register` - Cadastro de usuário
- `POST /auth/login` - Login

### Espaços de Despesas
- `POST /api/expense-spaces/create` - Criar novo espaço

### Gestão de Despesas
- `POST /api/expenses/create` - Criar nova despesa (com campo opcional `cardId`)
- `GET /api/expenses/space/{expenseSpaceId}` - Listar despesas do grupo
- `GET /api/expenses/{expenseId}` - Obter detalhes de uma despesa
- `PUT /api/expenses/{expenseId}` - Atualizar despesa existente
- `DELETE /api/expenses/{expenseId}` - Excluir despesa
- `PUT /api/expenses/{expenseId}/status` - Atualizar status de despesa (PENDING, PAID, OVERDUE, CANCELLED)
- `PUT /api/expenses/{expenseId}/installments/{installmentId}/status` - Atualizar status de parcela específica

### Cálculo de Saldos ⭐ **NOVO**
- `GET /api/expenses/balances/space/{expenseSpaceId}` - Calcular saldos líquidos entre usuários

### Gestão de Cartões ⭐ **NOVO**
- `POST /api/cards/space/{expenseSpaceId}` - Criar novo cartão de crédito
- `GET /api/cards/space/{expenseSpaceId}` - Listar todos os cartões do espaço
- `GET /api/cards/space/{expenseSpaceId}/my-cards` - Listar apenas meus cartões
- `GET /api/cards/{cardId}` - Obter detalhes de um cartão
- `PUT /api/cards/{cardId}` - Atualizar cartão (apenas proprietário)
- `DELETE /api/cards/{cardId}` - Excluir cartão (apenas proprietário)

### Convites
- `POST /api/invites/create` - Criar convite (OWNER/ADMIN apenas)
- `POST /api/invites/accept` - Aceitar convite via token

### Sistema de Convites via Link
- `GET /api/invite-link/{token}` - Obter informações do convite (público)
- `POST /api/invite-link/{token}/request` - Solicitar entrada no grupo
- `GET /api/invite-link/pending/{expenseSpaceId}` - Listar solicitações pendentes
- `POST /api/invite-link/approve/{inviteId}` - Aprovar solicitação
- `POST /api/invite-link/reject/{inviteId}` - Rejeitar solicitação

> 💡 **Dica:** Use o Swagger UI em `/docs` para testar os endpoints de forma interativa!

## Estrutura do Projeto

```
src/main/java/com/sharedexpenses/app/
├── config/          # Configurações (Security, etc.)
├── controller/      # Controllers REST
├── dto/            # DTOs para requisições/respostas
├── entity/         # Entidades JPA
├── repository/     # Repositórios Spring Data
├── security/       # Filtros de segurança
└── service/        # Lógica de negócio
```

## Requisitos Técnicos

- Java 21+
- Maven 3.9+
- Docker e Docker Compose
- PostgreSQL (via Docker)
- VS Code ou IntelliJ IDEA

## Executando o Projeto

### Com Docker (Recomendado)

```bash
# Clone o repositório
git clone <repo-url>
cd shared-expenses

# Configure as variáveis de ambiente
cp .env.example .env
# Edite o arquivo .env com suas configurações

# Execute com Docker Compose
docker-compose up --build
```

### Desenvolvimento Local

```bash
# Configure o PostgreSQL localmente ou use Docker apenas para o banco
docker run --name postgres -e POSTGRES_DB=shared_expenses -e POSTGRES_USER=youruser -e POSTGRES_PASSWORD=yourpassword -p 5432:5432 -d postgres:15

# Configure as variáveis de ambiente (substitua pelos seus valores)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/shared_expenses
export POSTGRES_USER=youruser
export POSTGRES_PASSWORD=yourpassword
export JWT_SECRET=your-super-secret-jwt-key-here-must-be-at-least-32-characters
export JWT_EXPIRATION=86400000

# Execute a aplicação
./mvnw spring-boot:run
```

## Próximos Passos

1. **Implementar relatórios e filtros para cartões**
   - Relatórios de gastos por cartão: `GET /api/cards/{id}/expenses?month=2025-09`
   - Filtros na listagem de despesas: `GET /api/expenses/space/{id}?cardId=5`
   - Analytics por cartão: `GET /api/cards/{id}/analytics/monthly`

2. **Expandir funcionalidades de cartões**
   - Sistema de limites de gastos por cartão
   - Notificações de vencimento de faturas
   - Dashboard consolidado de todos os cartões

3. **Implementar dashboard e relatórios financeiros**
   - Relatórios de gastos por período
   - Visualizações gráficas (dados estruturados para Chart.js/ApexCharts)
   - Analytics: `GET /api/expenses/analytics/monthly/{id}`, `/analytics/by-category/{id}`

4. **Funcionalidades avançadas de despesas**
   - Sistema de categorias
   - Lembretes de vencimento

5. **Desenvolver sistema de notificações**
   - Sistema de alertas simples

6. **Criar interface web**
   - Frontend para aceitar convites
   - Dashboard para visualização de gastos

## Exemplos de Uso

### 1. Criando um Cartão de Crédito
```bash
POST /api/cards/space/1
Content-Type: application/json
Authorization: Bearer <seu-token>

{
  "name": "Cartão Nubank Roxinho",
  "description": "Cartão principal para gastos da casa",
  "dueDay": 10
}
```

### 2. Criando Despesa Vinculada a Cartão
```bash
POST /api/expenses/create
Content-Type: application/json
Authorization: Bearer <seu-token>

{
  "title": "Supermercado",
  "description": "Compras da semana",
  "totalValue": 250.50,
  "date": "2025-09-01",
  "type": "SIMPLE",
  "expenseSpaceId": 1,
  "cardId": 5
}
```

### 3. Visualizando Saldos do Grupo
```bash
GET /api/expenses/balances/space/1
Authorization: Bearer <seu-token>

# Resposta mostra quem deve para quem, com valores líquidos
```

## Modelagem do Banco de Dados

### Usuário (User)
- ID, Nome, Email, Hash da Senha, Data de Criação

### Espaço de Despesas (ExpenseSpace)
- ID, Nome, Criado por User ID

### Participante (ExpenseParticipant)
- ID, User ID, ExpenseSpace ID, Role (OWNER/ADMIN/MEMBER), Data de Entrada

### Convite (ExpenseSpaceInvite)
- ID, ExpenseSpace ID, Token único, Criado por User ID, Data de Criação, Data de Expiração, Status
- Sistema de aprovação com campos: requested_by_user_id, approved_rejected_by_user_id, etc.

### Despesa (Expense) - *Estrutura criada*
- ID, Título, Descrição, Valor Total, Data, Tipo (Simples/Parcelada/Recorrente), Status (PENDING/PAID/OVERDUE/CANCELLED), Pago por User ID, Incluir pagador na divisão, ExpenseSpace ID, Card ID (opcional)

### Cartão (Card) - *Estrutura criada* ⭐ **NOVO**
- ID, Nome, Descrição, Dia de Vencimento (1-31), Proprietário (User ID), ExpenseSpace ID, Data de Criação/Atualização

### Parcela (ExpenseInstallment) - *Estrutura criada*
- ID, Expense ID, Número da parcela, Data de vencimento, Valor, Status (PENDING/PAID/OVERDUE/CANCELLED)

### Despesa Recorrente (RecurringExpense) - *Estrutura criada*
- ID, Descrição, Valor, Tipo de Recorrência, Data de início/fim, ExpenseSpace ID

## Papéis e Permissões

| Papel   | Permissões |
|---------|------------|
| OWNER   | Controle total do grupo, pode remover outros admins |
| ADMIN   | Convida/remove membros, adiciona despesas, gerencia configurações |
| MEMBER  | Adiciona despesas, visualiza saldos, participa das divisões |