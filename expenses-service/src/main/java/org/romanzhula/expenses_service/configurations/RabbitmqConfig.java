package org.romanzhula.expenses_service.configurations;

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

    @Value("${rabbitmq.queue.name.expense-added}")
    private String queueWalletReplenishedForExpensesService;


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
