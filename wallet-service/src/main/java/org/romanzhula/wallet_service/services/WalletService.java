package org.romanzhula.wallet_service.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.romanzhula.wallet_service.configurations.RabbitmqConfig;
import org.romanzhula.wallet_service.models.Wallet;
import org.romanzhula.wallet_service.models.events.BalanceOperationEvent;
import org.romanzhula.wallet_service.models.events.ExpensesResponseEvent;
import org.romanzhula.wallet_service.repositories.WalletRepository;
import org.romanzhula.wallet_service.responses.CommonWalletResponse;
import org.romanzhula.wallet_service.responses.WalletBalanceResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitmqConfig rabbitmqConfig;
    @Getter
    private final AddWalletService addWalletService;


    @Transactional(readOnly = true)
    public List<CommonWalletResponse> getAllWallets() {
        return walletRepository.findAll()
                .stream()
                .map(wallet -> new CommonWalletResponse(wallet.getUserId(), wallet.getBalance()))
                .collect(Collectors.toList())
                ;
    }

    @Transactional(readOnly = true)
    public CommonWalletResponse getWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(wallet -> new CommonWalletResponse(wallet.getUserId(), wallet.getBalance()))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId))
                ;
    }

    @Transactional(readOnly = true)
    public WalletBalanceResponse getBalanceByWalletId(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(wallet -> new WalletBalanceResponse(wallet.getBalance()))
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId))
                ;
    }

    @Transactional
    public String replenishBalance(BalanceOperationEvent balanceOperationEvent) {
        validateBalanceReplenishRequest(balanceOperationEvent);

        Wallet wallet = fetchWalletById(balanceOperationEvent.getUserId());
        replenishWalletBalance(wallet, balanceOperationEvent.getAmount());

        String description = String.format(
                "Operation replenish: +%s, account balance: %s",
                balanceOperationEvent.getAmount(),
                wallet.getBalance()
        );

        sendDataToQueueWalletReplenished(balanceOperationEvent, description);
        sendDataToQueueWalletReplenishedForExpensesService(balanceOperationEvent);

        return "Balance replenished successfully.";
    }


    @Transactional
    public String deductBalance(BalanceOperationEvent balanceOperationEvent) {
        validateDeductionRequest(balanceOperationEvent);

        Wallet wallet = fetchWalletById(balanceOperationEvent.getUserId());
        checkSufficientFunds(wallet, balanceOperationEvent.getAmount());

        wallet.setBalance(wallet.getBalance().subtract(balanceOperationEvent.getAmount()));
        walletRepository.save(wallet);

        String description = String.format(
                "Operation deduct: -%s, account balance: %s",
                balanceOperationEvent.getAmount(),
                wallet.getBalance()
        );

        sendDataToQueueWalletReplenished(balanceOperationEvent, description);

        return "Your balance successfully deducted!";
    }


    private void validateBalanceReplenishRequest(BalanceOperationEvent balanceOperationEvent) {
        if (balanceOperationEvent.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The top-up amount must be greater than 0.");
        }
    }

    private void replenishWalletBalance(Wallet wallet, BigDecimal amount) {
        if (wallet == null) {
            throw new RuntimeException("Wallet not found!");
        } else {
            wallet.setBalance(wallet.getBalance().add(amount));
        }

        walletRepository.save(wallet);
    }

    private Wallet fetchWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
    }

    private void validateDeductionRequest(BalanceOperationEvent balanceOperationEvent) {
        if (balanceOperationEvent.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The deduction amount must be greater than 0.");
        }

        if (balanceOperationEvent.getUserId() == null) {
            throw new IllegalArgumentException("Wallet ID cannot be null or empty.");
        }
    }

    private void checkSufficientFunds(Wallet wallet, BigDecimal deductionAmount) {
        if (wallet.getBalance().compareTo(deductionAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the wallet.");
        }
    }

    private void sendDataToQueueWalletReplenished(BalanceOperationEvent balanceOperationEvent, String description) {
        rabbitTemplate.convertAndSend(
                rabbitmqConfig.getQueueWalletReplenished(),
                new BalanceOperationEvent(
                        balanceOperationEvent.getUserId(),
                        balanceOperationEvent.getAmount(),
                        description
                )
        );
    }

    private void sendDataToQueueWalletReplenishedForExpensesService(BalanceOperationEvent balanceOperationEvent) {
        WalletBalanceResponse remainingBalance = getBalanceByWalletId(balanceOperationEvent.getUserId());

        ExpensesResponseEvent expensesResponseEvent = new ExpensesResponseEvent(
                balanceOperationEvent.getUserId(),
                "Wallet replenished",
                balanceOperationEvent.getAmount(),
                "Balance updated successfully",
                remainingBalance.getBalance()
        );

        rabbitTemplate.convertAndSend(
                rabbitmqConfig.getQueueWalletReplenishedForExpensesService(),
                expensesResponseEvent
        );
    }

}
