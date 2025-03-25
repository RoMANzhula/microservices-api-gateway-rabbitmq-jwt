package org.romanzhula.expenses_service.repositories;

import org.romanzhula.expenses_service.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpensesRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByUserId(UUID userId);

}
