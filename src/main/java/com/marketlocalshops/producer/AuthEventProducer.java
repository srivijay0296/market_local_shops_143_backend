package com.marketlocalshops.producer;

import com.marketlocalshops.config.rabbitmq.RabbitMQConfig;
import com.marketlocalshops.events.dto.AuthEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class AuthEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public AuthEventProducer(@Autowired(required = false) RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegisteredEvent(AuthEvent event) {
        event.setEventType("USER_REGISTERED");
        log.info("Publishing USER_REGISTERED event for user: {}", event.getUsername());
        if (rabbitTemplate != null) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MARKETPLACE, "auth.events.registered", event);
        }
    }

    public void publishUserLoginEvent(AuthEvent event) {
        event.setEventType("USER_LOGIN");
        log.info("Publishing USER_LOGIN event for user: {}", event.getUsername());
        if (rabbitTemplate != null) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MARKETPLACE, "auth.events.login", event);
        }
    }
}
