package com.marketlocalshops.listener;

import com.marketlocalshops.config.rabbitmq.RabbitMQConfig;
import com.marketlocalshops.events.dto.AuthEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;

@Service
@ConditionalOnProperty(name = "messaging.rabbitmq.enabled", havingValue = "true")
@Slf4j
public class AuthEventListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUTH_EVENTS, ackMode = "MANUAL")
    public void handleAuthEvent(AuthEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("Received Auth Event: {} for user: {}", event.getEventType(), event.getUsername());
            
            // Example processing logic...
            if ("USER_REGISTERED".equals(event.getEventType())) {
                log.info("Sending welcome email to {}", event.getEmail());
            }

            // Acknowledge the message
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Failed to process Auth Event: {}", e.getMessage());
            // Nack and do not requeue, sends to DLQ
            channel.basicNack(tag, false, false);
        }
    }
}
