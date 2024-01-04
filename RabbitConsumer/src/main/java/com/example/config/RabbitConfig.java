package com.example.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class RabbitConfig {
    @Bean
    @Scope("prototype")
    @ConfigurationProperties(prefix = "spring.rabbitmq")
    public CachingConnectionFactory CachingConnectionFactory(){
        return new CachingConnectionFactory();
    }

    @Bean
    @Scope("prototype")
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory){
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.rabbitmq.listener.simple")
    public MessageListenerConfig MessageListenerConfig(){
        return new MessageListenerConfig();
    }

    @Getter @Setter
    public static class MessageListenerConfig{
        private int concurrency=30;
        private int maxConcurrency=50;
    }
}
