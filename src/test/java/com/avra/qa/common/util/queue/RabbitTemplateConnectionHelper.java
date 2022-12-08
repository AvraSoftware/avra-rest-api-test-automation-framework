package com.avra.qa.common.util.queue;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RabbitTemplateConnectionHelper {

    @Autowired
    @Qualifier("connectionFactory")
    org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory;
    @Autowired
    @Qualifier("bsConnectionFactory")
    org.springframework.amqp.rabbit.connection.ConnectionFactory secondConnectionFactory;

    public void publishSKEvent(String eventType, String entityType, String queueName, String payload) throws InterruptedException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("event", eventType);
        messageProperties.setHeader("entity-type", entityType);
        messageProperties.setHeader("major-version", 1);
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

        Message message = new Message(payload.getBytes(), messageProperties);

        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.convertAndSend(queueName, message);

        Thread.sleep(400);
    }

    public void publishBSEvent(String queueName, String payload) throws Exception {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

        Message message = new Message(payload.getBytes(), messageProperties);
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(secondConnectionFactory);
        rabbitTemplate.convertAndSend(queueName, message);

        Thread.sleep(400);
    }

}
