package com.example.config;

import com.example.config.RabbitConfig.MessageListenerConfig;
import com.example.resource.RabbitConsumer;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    @Autowired
    private MessageListenerConfig messageListenerConfig;

    @Value("${spring.rabbitmq.queue-name:TEST}")
    private String queueName;

    @Bean
    public Queue queue(){
        return new Queue(queueName, true, false, false);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public boolean createQueue(Queue queue, RabbitAdmin rabbitAdmin){
        rabbitAdmin.declareQueue(queue);
        return true;
    }
    

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(RabbitTemplate rabbitTemplate, RabbitConsumer rabbitListener){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		container.setConcurrentConsumers(messageListenerConfig.getConcurrency());
		container.setMaxConcurrentConsumers(messageListenerConfig.getMaxConcurrency());

		container.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        container.addQueueNames(queueName);
		container.setMessageListener(new MessageListenerAdapter(rabbitListener, "Consumer"));

		return container;
    }
    
}
