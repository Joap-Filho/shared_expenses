# Sistema de Status de Despesas - Implementado ✅

## 📊 **Novos Status para Despesas**

### Enum ExpenseStatus
```java
PENDING,    // Pendente - despesa criada mas ainda não paga
PAID,       // Pago - despesa foi quitada  
OVERDUE,    // Em atraso - passou da data e não foi paga
CANCELLED   // Cancelada - despesa foi cancelada
```

## 🗄️ **Banco de Dados**

### Migração V10
```sql
ALTER TABLE expense ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
```

## 🔧 **Novos Endpoints**

### 1. Atualizar Status de Despesa
```http
PUT /api/expenses/{expenseId}/status
Content-Type: application/json

{
    "status": "PAID"
}
```

**Valores aceitos:** `PENDING`, `PAID`, `OVERDUE`, `CANCELLED`

### 2. Atualizar Status de Parcela
```http
PUT /api/expenses/installments/{installmentId}/status
Content-Type: application/json

{
    "paid": true
}
```

## 📤 **Response Atualizado**

### ExpenseResponse agora inclui:
```json
{
    "id": 1,
    "title": "Aluguel",
    "description": "Aluguel do apartamento",
    "totalValue": 1200.00,
    "date": "2025-01-01",
    "status": "PENDING",
    "paidByUserName": "João",
    "type": "SIMPLE",
    ...
}
```

## 🤖 **Automatização**

### ExpenseStatusService
- **Executa diariamente às 6:00 AM**
- **Marca como OVERDUE**: Despesas PENDING com mais de 7 dias de atraso
- **Log**: Registra quantas despesas foram marcadas como atrasadas

## 💡 **Funcionalidades**

### ✅ **Implementado**
1. **Status em despesas simples e recorrentes**
2. **Status em parcelas** (já existia, mantido)
3. **Endpoints PUT para atualizar status**
4. **Validação de permissões** (só participantes do grupo)
5. **Marcação automática de OVERDUE**
6. **Migração de banco compatível**

### 🎯 **Como Usar**

#### Marcar despesa como paga:
```bash
curl -X PUT /api/expenses/123/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"status": "PAID"}'
```

#### Marcar parcela como paga:
```bash
curl -X PUT /api/expenses/installments/456/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"paid": true}'
```

## 🔒 **Segurança**
- ✅ **Validação de participação no grupo**
- ✅ **Autenticação JWT obrigatória**
- ✅ **Validação de dados de entrada**

## 📊 **Status Automático**
- **PENDING**: Estado inicial de toda despesa
- **PAID**: Usuário marca manualmente
- **OVERDUE**: Sistema marca automaticamente (>7 dias de atraso)
- **CANCELLED**: Usuário marca manualmente

Implementação **completa e funcional**! 🎉
