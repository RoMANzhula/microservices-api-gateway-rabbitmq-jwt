package org.romanzhula.journal_service.configurations;

import lombok.Getter;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@Getter
@Configuration
public class RabbitmqConfig {

    @Value("${rabbitmq.queue.name.wallet-updated}")
    private String queueWalletBalanceUpdated;


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ErrorHandler rabbitErrorHandler() {
        return (Throwable t) -> {
            throw new AmqpRejectAndDontRequeueException("Error processing message", t);
        };
    }

}
