package com.example.domain.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queues.created-products}")
    private String createdProductsQueue;

    @Value("${rabbitmq.queues.updated-products}")
    private String updatedProductsQueue;

    @Value("${rabbitmq.queues.deleted-products}")
    private String deletedProductsQueue;


    @Bean
    public Queue createdProductsQueue() {
        return new Queue(createdProductsQueue, false);
    }

    @Bean
    public Queue updatedProductsQueue() {
        return new Queue(updatedProductsQueue, false);
    }

    @Bean
    public Queue deletedProductsQueue() {
        return new Queue(deletedProductsQueue, false);
    }
}
