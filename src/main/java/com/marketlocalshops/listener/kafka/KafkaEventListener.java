package com.marketlocalshops.listener.kafka;

import com.marketlocalshops.events.dto.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Service
@ConditionalOnProperty(name = "messaging.kafka.enabled", havingValue = "true")
@Slf4j
public class KafkaEventListener {

    // Kafka is disabled by default in application.yml
    // This listener is prepared for when Kafka is activated

    @KafkaListener(topics = "user-events", groupId = "market-local-shops-group")
    public void listenUserEvents(BaseEvent event) {
        log.info("Received Kafka User Event: {}", event);
    }
}
