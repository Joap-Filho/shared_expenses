package com.sharedexpenses.app.service;

import com.sharedexpenses.app.dto.CreateExpenseRequest;
import com.sharedexpenses.app.dto.ExpenseResponse;
import com.sharedexpenses.app.entity.*;
import com.sharedexpenses.app.entity.enums.ExpenseType;
import com.sharedexpenses.app.entity.enums.RoleType;
import com.sharedexpenses.app.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseSpaceRepository expenseSpaceRepository;

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;

    @Autowired
    private ExpenseInstallmentRepository expenseInstallmentRepository;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Cria uma nova despesa
     */
    public Expense createExpense(CreateExpenseRequest request, String userEmail) {
        // Validar se usuário é participante do grupo
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, request.getExpenseSpaceId())) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }

        // Buscar entidades necessárias
        User paidByUser = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ExpenseSpace expenseSpace = expenseSpaceRepository.findById(request.getExpenseSpaceId())
            .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        // Criar despesa
        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setTotalValue(request.getTotalValue());
        expense.setDate(request.getDate());
        expense.setType(request.getType());
        expense.setPaidBy(paidByUser);
        expense.setIncludePayerInSplit(request.isIncludePayerInSplit());
        expense.setExpenseSpace(expenseSpace);

        // Definir beneficiários
        Set<User> beneficiaries = determineBeneficiaries(request, expenseSpace);
        expense.setBeneficiaries(beneficiaries);

        // Salvar despesa
        Expense savedExpense = expenseRepository.save(expense);

        // Processar tipo específico da despesa
        if (request.getType() == ExpenseType.INSTALLMENT && request.getInstallments() != null) {
            createInstallments(savedExpense, request.getInstallments());
        }

        return savedExpense;
    }

    /**
     * Determina quem são os beneficiários da despesa
     */
    private Set<User> determineBeneficiaries(CreateExpenseRequest request, ExpenseSpace expenseSpace) {
        Set<User> beneficiaries = new HashSet<>();

        if (request.getBeneficiaryIds() != null && !request.getBeneficiaryIds().isEmpty()) {
            // Beneficiários específicos
            for (Long beneficiaryId : request.getBeneficiaryIds()) {
                User user = userRepository.findById(beneficiaryId)
                    .orElseThrow(() -> new RuntimeException("Beneficiário com ID " + beneficiaryId + " não encontrado"));
                
                // Verificar se é participante do grupo
                boolean isParticipant = expenseParticipantRepository
                    .findByUserIdAndExpenseSpaceId(beneficiaryId, expenseSpace.getId())
                    .isPresent();
                
                if (!isParticipant) {
                    throw new RuntimeException("Usuário " + user.getName() + " não é participante do grupo");
                }
                
                beneficiaries.add(user);
            }
        } else {
            // Todos os participantes do grupo
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseSpaceId(expenseSpace.getId());
            for (ExpenseParticipant participant : participants) {
                beneficiaries.add(participant.getUser());
            }
        }

        return beneficiaries;
    }

    /**
     * Cria parcelas para despesa parcelada
     */
    private void createInstallments(Expense expense, Integer numberOfInstallments) {
        BigDecimal installmentValue = expense.getTotalValue().divide(
            BigDecimal.valueOf(numberOfInstallments), 
            2, 
            RoundingMode.HALF_UP
        );

        for (int i = 1; i <= numberOfInstallments; i++) {
            ExpenseInstallment installment = new ExpenseInstallment();
            installment.setExpense(expense);
            installment.setNumber(i);
            installment.setDueDate(expense.getDate().plusMonths(i - 1)); // Uma parcela por mês
            installment.setValue(installmentValue);
            installment.setPaid(false);

            expenseInstallmentRepository.save(installment);
        }
    }

    /**
     * Busca despesas de um grupo específico
     */
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesBySpace(Long expenseSpaceId, String userEmail) {
        // Validar se usuário é participante do grupo
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, expenseSpaceId)) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }

        List<Expense> expenses = expenseRepository.findByExpenseSpaceId(expenseSpaceId);
        return expenses.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Busca despesa por ID
     */
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long expenseId, String userEmail) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        // Validar se usuário é participante do grupo
        if (!authorizationService.isUserParticipantOfExpenseSpace(userEmail, expense.getExpenseSpace().getId())) {
            throw new RuntimeException("Usuário não é participante deste grupo");
        }

        return convertToResponse(expense);
    }

    /**
     * Atualiza uma despesa existente
     */
    public Expense updateExpense(Long expenseId, CreateExpenseRequest request, String userEmail) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        // Validar permissões - apenas quem pagou ou admins/owners podem editar
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean canEdit = expense.getPaidBy().getId().equals(user.getId()) ||
            hasAdminPermission(userEmail, expense.getExpenseSpace().getId());

        if (!canEdit) {
            throw new RuntimeException("Sem permissão para editar esta despesa");
        }

        // Atualizar campos
        expense.setDescription(request.getDescription());
        expense.setTotalValue(request.getTotalValue());
        expense.setDate(request.getDate());
        expense.setIncludePayerInSplit(request.isIncludePayerInSplit());

        // Atualizar beneficiários
        Set<User> beneficiaries = determineBeneficiaries(request, expense.getExpenseSpace());
        expense.setBeneficiaries(beneficiaries);

        return expenseRepository.save(expense);
    }

    /**
     * Remove uma despesa
     */
    public void deleteExpense(Long expenseId, String userEmail) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        // Validar permissões - apenas quem pagou ou admins/owners podem excluir
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean canDelete = expense.getPaidBy().getId().equals(user.getId()) ||
            hasAdminPermission(userEmail, expense.getExpenseSpace().getId());

        if (!canDelete) {
            throw new RuntimeException("Sem permissão para excluir esta despesa");
        }

        expenseRepository.delete(expense);
    }

    /**
     * Verifica se usuário tem permissão de admin
     */
    private boolean hasAdminPermission(String userEmail, Long expenseSpaceId) {
        try {
            RoleType role = authorizationService.getUserRole(userEmail, expenseSpaceId);
            return role == RoleType.OWNER || role == RoleType.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Converte entidade Expense para ExpenseResponse
     */
    public ExpenseResponse convertToResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        
        response.setId(expense.getId());
        response.setDescription(expense.getDescription());
        response.setTotalValue(expense.getTotalValue());
        response.setDate(expense.getDate());
        response.setCreatedAt(expense.getCreatedAt());
        response.setType(expense.getType());
        response.setPaidByUserName(expense.getPaidBy().getName());
        response.setPaidByUserEmail(expense.getPaidBy().getEmail());
        response.setIncludePayerInSplit(expense.isIncludePayerInSplit());
        response.setExpenseSpaceId(expense.getExpenseSpace().getId());
        response.setExpenseSpaceName(expense.getExpenseSpace().getName());

        // Calcular divisão
        Set<User> beneficiaries = expense.getBeneficiaries();
        if (beneficiaries != null && !beneficiaries.isEmpty()) {
            response.setTotalParticipants(beneficiaries.size());
            BigDecimal valuePerPerson = expense.getTotalValue().divide(
                BigDecimal.valueOf(beneficiaries.size()), 
                2, 
                RoundingMode.HALF_UP
            );
            response.setValuePerPerson(valuePerPerson);

            // Criar lista de beneficiários com valores
            List<ExpenseResponse.BeneficiaryInfo> beneficiaryInfos = beneficiaries.stream()
                .map(user -> new ExpenseResponse.BeneficiaryInfo(
                    user.getId(), 
                    user.getName(), 
                    user.getEmail(), 
                    valuePerPerson
                ))
                .collect(Collectors.toList());
            
            response.setBeneficiaries(beneficiaryInfos);
        }

        // Adicionar detalhes de parcelas se for despesa parcelada
        if (expense.getType() == ExpenseType.INSTALLMENT) {
            List<ExpenseInstallment> installments = expenseInstallmentRepository
                .findByExpenseIdOrderByNumber(expense.getId());
            
            if (!installments.isEmpty()) {
                response.setInstallments(installments.size());
                
                List<ExpenseResponse.InstallmentInfo> installmentInfos = installments.stream()
                    .map(inst -> new ExpenseResponse.InstallmentInfo(
                        inst.getId(),
                        inst.getNumber(),
                        inst.getDueDate(),
                        inst.getValue(),
                        inst.isPaid()
                    ))
                    .collect(Collectors.toList());
                
                response.setInstallmentDetails(installmentInfos);
            }
        }

        return response;
    }
}
