package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import lombok.Getter;

@Configuration
@Getter
public class RabbitConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(RabbitConfig.class);

    @Value("${spring.rabbitmq.queue-name}")
    private String queueName;
    
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
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue queue(){
        return new Queue(queueName, true, false, false);
    }   

    @Bean
    public boolean createQueue(Queue queue, RabbitAdmin rabbitAdmin){
        rabbitAdmin.declareQueue(queue);
        return true;
    }
}
