package org.romanzhula.wallet_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.romanzhula.wallet_service.configurations.RabbitmqConfig;
import org.romanzhula.wallet_service.models.User;
import org.romanzhula.wallet_service.models.Wallet;
import org.romanzhula.wallet_service.repositories.WalletRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@Slf4j
@RequiredArgsConstructor
public class AddWalletService {

    private final RabbitmqConfig rabbitmqConfig;
    private final WalletRepository walletRepository;

    @RabbitListener(queues = "#{@rabbitmqConfig.getQueueUserCreated}")
    public void receiveQueueUserCreated(User user) {
        handleUserCreated(rabbitmqConfig.getQueueUserCreated(), user);
    }

    @Transactional
    private void handleUserCreated(String queueName, User user) {
        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setBalance(BigDecimal.valueOf(0.0));

        // here we can do any user validation via it data in "user" and send message to email(smtp)

        walletRepository.save(wallet);

        log.info("Wallet created for user: {}", user.getUsername());
    }

}
