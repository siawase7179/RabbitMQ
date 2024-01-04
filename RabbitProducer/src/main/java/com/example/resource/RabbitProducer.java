package com.example.resource;

import com.example.config.RabbitConfig;
import com.example.vo.DataVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitProducer implements Runnable {
    public static final Logger LOGGER = LoggerFactory.getLogger(RabbitProducer.class);    

    @Autowired
    private RabbitConfig rabbitConfig;
    

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private ExecutorService workerPool;
    private ObjectMapper mapper = new ObjectMapper();
    private int sequence = 0;

    @PostConstruct
    public void init(){
        workerPool = Executors.newCachedThreadPool();
        workerPool.execute(this);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        LOGGER.info("Producer Started.");
        int i=0;
        long startTime = System.currentTimeMillis()/1000;

        while (true){
            if (System.currentTimeMillis()/1000 - startTime >= 180){
                break;
            }
            sequence = sequence > 10000000 ? 0 : sequence + 1;
            try {
                DataVO data = DataVO.builder().sequence(String.format("%08d", sequence)).enqueTime(System.currentTimeMillis()/1000).build();
                LOGGER.debug("(enque) {}", data.toString());
                rabbitTemplate.send(rabbitConfig.getQueueName(), new Message(mapper.writeValueAsBytes(data), new MessageProperties()));
            } catch (AmqpException | JsonProcessingException e) {
                // TODO Auto-generated catch block
                LOGGER.error("error", e);
            }

            
        }
    }
}
