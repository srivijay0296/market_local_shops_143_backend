package com.marketlocalshops.producer;

import com.marketlocalshops.config.rabbitmq.RabbitMQConfig;
import com.marketlocalshops.events.dto.MarketEvent;
import com.marketlocalshops.events.dto.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class CatalogEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public CatalogEventProducer(@Autowired(required = false) RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMarketCreatedEvent(MarketEvent event) {
        event.setEventType("MARKET_CREATED");
        log.info("Publishing MARKET_CREATED event for market: {}", event.getName());
        if (rabbitTemplate != null) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MARKETPLACE, "market.events.created", event);
        }
    }

    public void publishProductCreatedEvent(ProductEvent event) {
        event.setEventType("PRODUCT_CREATED");
        log.info("Publishing PRODUCT_CREATED event for product: {}", event.getName());
        if (rabbitTemplate != null) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MARKETPLACE, "product.events.created", event);
        }
    }
}
