package com.marketlocalshops.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "messaging.rabbitmq.enabled", havingValue = "true")
public class RabbitMQConfig {

    public static final String EXCHANGE_MARKETPLACE = "marketplace.exchange";
    
    // Auth
    public static final String QUEUE_AUTH_EVENTS = "auth.events.queue";
    public static final String ROUTING_KEY_AUTH = "auth.events.#";
    
    // Market
    public static final String QUEUE_MARKET_EVENTS = "market.events.queue";
    public static final String ROUTING_KEY_MARKET = "market.events.#";
    
    // Product
    public static final String QUEUE_PRODUCT_EVENTS = "product.events.queue";
    public static final String ROUTING_KEY_PRODUCT = "product.events.#";
    
    // Order
    public static final String QUEUE_ORDER_EVENTS = "order.events.queue";
    public static final String ROUTING_KEY_ORDER = "order.events.#";

    // DLQ
    public static final String DLX_EXCHANGE = "dlx.marketplace.exchange";
    public static final String DLQ_QUEUE = "marketplace.dlq";

    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue dlqQueue() {
        return new Queue(DLQ_QUEUE);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlxExchange()).with("#");
    }

    @Bean
    public TopicExchange marketplaceExchange() {
        return new TopicExchange(EXCHANGE_MARKETPLACE);
    }

    private Queue createQueueWithDLQ(String queueName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", queueName + ".dlq")
                .build();
    }

    @Bean
    public Queue authQueue() { return createQueueWithDLQ(QUEUE_AUTH_EVENTS); }

    @Bean
    public Queue marketQueue() { return createQueueWithDLQ(QUEUE_MARKET_EVENTS); }

    @Bean
    public Queue productQueue() { return createQueueWithDLQ(QUEUE_PRODUCT_EVENTS); }

    @Bean
    public Queue orderQueue() { return createQueueWithDLQ(QUEUE_ORDER_EVENTS); }

    @Bean
    public Binding authBinding() {
        return BindingBuilder.bind(authQueue()).to(marketplaceExchange()).with(ROUTING_KEY_AUTH);
    }

    @Bean
    public Binding marketBinding() {
        return BindingBuilder.bind(marketQueue()).to(marketplaceExchange()).with(ROUTING_KEY_MARKET);
    }

    @Bean
    public Binding productBinding() {
        return BindingBuilder.bind(productQueue()).to(marketplaceExchange()).with(ROUTING_KEY_PRODUCT);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(marketplaceExchange()).with(ROUTING_KEY_ORDER);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
