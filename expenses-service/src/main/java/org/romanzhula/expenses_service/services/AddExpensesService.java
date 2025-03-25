package org.romanzhula.expenses_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.romanzhula.expenses_service.configurations.RabbitmqConfig;
import org.romanzhula.expenses_service.models.Expense;
import org.romanzhula.expenses_service.models.events.ExpensesRequestEvent;
import org.romanzhula.expenses_service.repositories.ExpensesRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddExpensesService {

    private final RabbitmqConfig rabbitmqConfig;
    private final ExpensesRepository expensesRepository;

    @RabbitListener(queues = "#{@rabbitmqConfig.getQueueWalletReplenishedForExpensesService}")
    public void receiveQueueExpensesSave(ExpensesRequestEvent expensesRequestEvent) {
        handleExpensesSaveFromWallet(rabbitmqConfig.getQueueWalletReplenishedForExpensesService(), expensesRequestEvent);
    }

    @Transactional
    private void handleExpensesSaveFromWallet(String queueName, ExpensesRequestEvent expensesRequestEvent) {
        Expense expense = new Expense();
        expense.setUserId(expensesRequestEvent.getUserId());
        expense.setTitle(expensesRequestEvent.getTitle());
        expense.setAmount(expensesRequestEvent.getAmount());
        expense.setMessage(expensesRequestEvent.getMessage());
        expense.setRemainingBalance(expensesRequestEvent.getRemainingBalance());

        expensesRepository.save(expense);

        log.info("Expense created for wallet: {}, from Queue: {}", expensesRequestEvent.getUserId(), queueName);
    }

}
