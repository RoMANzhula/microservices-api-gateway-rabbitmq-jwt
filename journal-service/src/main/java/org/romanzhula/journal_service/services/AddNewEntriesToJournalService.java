package org.romanzhula.journal_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.romanzhula.journal_service.configurations.RabbitmqConfig;
import org.romanzhula.journal_service.models.JournalEntry;
import org.romanzhula.journal_service.models.events.JournalEntryEvent;
import org.romanzhula.journal_service.repositories.JournalRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AddNewEntriesToJournalService {

    private final RabbitmqConfig rabbitmqConfig;
    private final JournalRepository journalRepository;


    @RabbitListener(queues = "#{@rabbitmqConfig.getQueueWalletBalanceUpdated}")
    public void receiveQueueWalletUpdated(JournalEntryEvent journalEntryEvent) {
        handleJournalEntryFromWallet(rabbitmqConfig.getQueueWalletBalanceUpdated(), journalEntryEvent);
    }

    @Transactional
    private void handleJournalEntryFromWallet(String queueName, JournalEntryEvent journalEntryEvent) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setUserId(journalEntryEvent.getUserId());
        journalEntry.setDescription(journalEntryEvent.getDescription());

        journalRepository.save(journalEntry);

        log.info("Journal entry created for wallet: {}, from Queue: {}, amount different: {}",
                journalEntryEvent.getUserId(),
                queueName,
                journalEntryEvent.getAmount()
        );
    }

}
