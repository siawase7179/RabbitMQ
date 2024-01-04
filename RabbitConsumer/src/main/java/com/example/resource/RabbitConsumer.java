package com.example.resource;

import com.rabbitmq.client.Channel;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;

import com.example.vo.DataVO;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;


@Component
public class RabbitConsumer implements ChannelAwareMessageListener{
    public static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumer.class);    

    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init(){
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        // TODO Auto-generated method stub
        DataVO testDataVO = mapper.readValue(message.getBody(), DataVO.class);
        testDataVO.setDequeTime(System.currentTimeMillis()/1000);
        LOGGER.info("(deque) {}, queue:{}, exchange:{}", testDataVO.toString(), message.getMessageProperties().getConsumerQueue(), message.getMessageProperties().getReceivedExchange());
    }
    
}
