# Como usar a documentação Swagger

## 1. Inicie a aplicação
```bash
docker-compose up --build
# ou
./mvnw spring-boot:run
```

## 2. Acesse a documentação
- Swagger UI: http://localhost:8080/docs
- OpenAPI spec: http://localhost:8080/api-docs

## 3. Teste os endpoints

### Passo 1: Registrar usuário
1. No Swagger UI, encontre `POST /auth/register`
2. Clique em "Try it out"
3. Insira os dados:
```json
{
  "name": "João Silva",
  "email": "joao@email.com", 
  "password": "123456"
}
```
4. Clique em "Execute"
5. Copie o token da resposta

### Passo 2: Configurar autenticação
1. Clique no botão "Authorize" (cadeado) no topo da página
2. Insira: `Bearer SEU_TOKEN_AQUI`
3. Clique em "Authorize"

### Passo 3: Testar endpoints protegidos
Agora você pode testar endpoints como:
- `POST /api/expense-spaces/create` - Criar espaço de despesas
- `POST /api/invites/create` - Criar convites
- `POST /api/invites/accept` - Aceitar convites

## Recursos do Swagger UI
- ✅ Interface interativa para todos os endpoints
- ✅ Documentação automática dos parâmetros
- ✅ Exemplos de request/response
- ✅ Suporte completo ao JWT
- ✅ Validação em tempo real
