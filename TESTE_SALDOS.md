# 💰 Teste do Sistema de Saldos Dinâmicos

## 🎯 **Objetivo**
Validar o cálculo automático de saldos líquidos entre usuários com compensação automática de dívidas mútuas.

---

## 🧪 **Cenário de Teste**

### **Setup Inicial:**
1. **Grupo:** "republica teste 1" (ID: 1)
2. **Participantes:** João, Maria, Pedro
3. **Despesas existentes no sistema**

### **Exemplo de Despesas:**
```
1. Pizza - R$ 120,00 (Pago por João)
   - Beneficiários: João, Maria, Pedro
   - Cada um deve: R$ 40,00

2. Uber - R$ 60,00 (Pago por Maria)  
   - Beneficiários: João, Maria, Pedro
   - Cada um deve: R$ 20,00

3. Supermercado - R$ 90,00 (Pago por Pedro)
   - Beneficiários: Maria, Pedro (João não estava)
   - Cada um deve: R$ 45,00
```

---

## 📊 **Cálculo Esperado**

### **João:**
- **Deve:** Maria R$ 20 (Uber) = R$ 20,00
- **Deve:** Pedro R$ 40 (Pizza) = R$ 40,00
- **Recebe:** Maria R$ 40 (Pizza) = R$ 40,00
- **Recebe:** Pedro R$ 40 (Pizza) = R$ 40,00
- **Saldo líquido:** João deve R$ 20,00 para Maria

### **Maria:**
- **Deve:** João R$ 40 (Pizza) = R$ 40,00  
- **Deve:** Pedro R$ 45 (Supermercado) + R$ 40 (Pizza) = R$ 85,00
- **Recebe:** João R$ 20 (Uber) = R$ 20,00
- **Recebe:** Pedro R$ 20 (Uber) = R$ 20,00
- **Saldo líquido:** Maria deve R$ 65,00 para Pedro, recebe R$ 20,00 de João

### **Pedro:**
- **Deve:** João R$ 40 (Pizza) = R$ 40,00
- **Deve:** Maria R$ 20 (Uber) = R$ 20,00  
- **Recebe:** João R$ 40 (Pizza) = R$ 40,00
- **Recebe:** Maria R$ 45 (Supermercado) + R$ 40 (Pizza) = R$ 85,00
- **Saldo líquido:** Pedro recebe R$ 65,00 de Maria

---

## 🔍 **Teste da API**

### **Request:**
```http
GET /api/expenses/balances/space/1
Authorization: Bearer {seu-jwt-token}
```

### **Response Esperada:**
```json
[
  {
    "fromUserName": "João",
    "fromUserEmail": "joao@email.com",
    "toUserName": "Maria", 
    "toUserEmail": "maria@email.com",
    "netAmount": 20.00,
    "details": {
      "totalFromOwes": 20.00,
      "totalToOwes": 40.00,
      "breakdown": [
        {
          "expenseId": 2,
          "description": "Uber",
          "date": "01/09/2025",
          "paidBy": "Maria",
          "amount": 20.00,
          "direction": "owes"
        },
        {
          "expenseId": 1,
          "description": "Pizza",
          "date": "31/08/2025", 
          "paidBy": "João",
          "amount": 40.00,
          "direction": "receives"
        }
      ]
    }
  },
  {
    "fromUserName": "Maria",
    "fromUserEmail": "maria@email.com",
    "toUserName": "Pedro",
    "toUserEmail": "pedro@email.com", 
    "netAmount": 65.00,
    "details": {
      "totalFromOwes": 85.00,
      "totalToOwes": 20.00,
      "breakdown": [
        {
          "expenseId": 1,
          "description": "Pizza",
          "date": "31/08/2025",
          "paidBy": "João", 
          "amount": 40.00,
          "direction": "owes"
        },
        {
          "expenseId": 3,
          "description": "Supermercado",
          "date": "02/09/2025",
          "paidBy": "Pedro",
          "amount": 45.00,
          "direction": "owes"
        },
        {
          "expenseId": 2,
          "description": "Uber",
          "date": "01/09/2025",
          "paidBy": "Maria",
          "amount": 20.00,
          "direction": "receives"
        }
      ]
    }
  }
]
```

---

## ✅ **Validações**

### **1. Cálculo Correto:**
- ✅ Soma todas as dívidas de cada pessoa
- ✅ Compensa dívidas mútuas automaticamente  
- ✅ Mostra apenas saldos líquidos diferentes de zero

### **2. Estrutura da Resposta:**
- ✅ `fromUser` → quem deve
- ✅ `toUser` → para quem deve
- ✅ `netAmount` → valor líquido
- ✅ `breakdown` → detalhamento de cada despesa

### **3. Breakdown Detalhado:**
- ✅ `direction: "owes"` → quando fromUser deve
- ✅ `direction: "receives"` → quando fromUser tem crédito
- ✅ Valores batem com o cálculo manual

### **4. Segurança:**
- ✅ Apenas participantes do grupo podem acessar
- ✅ JWT obrigatório
- ✅ Validação de permissões

---

## 🎯 **Resultado Final Esperado**

**Resumo das dívidas:**
- João deve R$ 20,00 para Maria
- Maria deve R$ 65,00 para Pedro
- Pedro não deve nada (recebe R$ 65,00 de Maria)

**Total das transações para quitar tudo:**
1. João paga R$ 20,00 para Maria
2. Maria paga R$ 65,00 para Pedro
3. **Grupo quitado!** ✅

---

## 🚀 **Como Testar:**

1. **Acesse o Swagger:** http://localhost:8080/docs
2. **Faça login** para obter JWT token
3. **Configure Authorization:** Bearer {token}
4. **Execute:** GET /api/expenses/balances/space/1
5. **Valide** se response bate com esperado

**A mágica é que agora o usuário vê de forma clara e automática quem deve quanto para quem, sem fazer contas mentais!** 🧮✨
