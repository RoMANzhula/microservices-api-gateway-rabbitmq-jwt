package org.romanzhula.user_service.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@Slf4j
@Configuration
public class RabbitmqConfig {

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.queue.name.user-created}")
    private String queueUserCreated;


    @Bean
    public Queue userCreatedQueue() {
        return new Queue(queueUserCreated, true); // durable "false" - this queue will be removed after stop server
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            CachingConnectionFactory cachingConnectionFactory,
            ErrorHandler rabbitErrorHandler
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setErrorHandler(rabbitErrorHandler);
        return factory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        // to run rabbitmq while running our application
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);

        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(cachingConnectionFactory());
    }

    @Bean
    public ErrorHandler rabbitErrorHandler() {
        return (Throwable t) -> {
            throw new AmqpRejectAndDontRequeueException("Error processing message", t);
        };
    }

    @Bean
    public CommandLineRunner createQueueAtStartup(RabbitAdmin rabbitAdmin, Queue userCreatedQueue) {
        return args -> {
            rabbitAdmin.declareQueue(userCreatedQueue);

            log.info("Queue {} is created or already exist.", userCreatedQueue.getName());
        };
    }

}
