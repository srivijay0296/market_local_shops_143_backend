package com.marketlocalshops.producer.kafka;

import com.marketlocalshops.events.dto.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class KafkaEventProducer {

    @SuppressWarnings("unused")
    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    public KafkaEventProducer(@Autowired(required = false) KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String topic, String key, BaseEvent event) {
        log.info("Publishing event to Kafka topic {}: {}", topic, event);
        // Uncomment when Kafka is enabled:
        // kafkaTemplate.send(topic, key, event);
    }
}
