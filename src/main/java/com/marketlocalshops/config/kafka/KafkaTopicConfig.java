package com.marketlocalshops.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "messaging.kafka.enabled", havingValue = "true")
public class KafkaTopicConfig {
    
    // Kept disabled by default in application.yml.
    // Topics to be created when Kafka is fully enabled.

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name("user-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic productEventsTopic() {
        return TopicBuilder.name("product-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name("order-events").partitions(3).replicas(1).build();
    }
}
