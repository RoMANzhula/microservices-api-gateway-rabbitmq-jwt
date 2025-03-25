package org.romanzhula.expenses_service.services;

import lombok.RequiredArgsConstructor;
import org.romanzhula.expenses_service.repositories.ExpensesRepository;
import org.romanzhula.expenses_service.responses.ExpensesResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpensesService {

    private final ExpensesRepository expensesRepository;

    @Transactional(readOnly = true)
    public List<ExpensesResponse> getAllExpensesByUserId(String userId) {
        return expensesRepository.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(expense -> new ExpensesResponse(
                        expense.getId(),
                        expense.getUserId(),
                        expense.getTitle(),
                        expense.getAmount(),
                        expense.getMessage(),
                        expense.getRemainingBalance()
                ))
                .toList()
                ;
    }

}
