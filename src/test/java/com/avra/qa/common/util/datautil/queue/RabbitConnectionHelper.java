package com.avra.qa.common.util.datautil.queue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitConnectionHelper {

    protected String rabbitmqUrl;
    protected Integer rabbitmqPort;
    protected String rabbitmqUser;
    protected String rabbitmqPass;

    protected ConnectionFactory inputRabbitConnectionFactory;
    protected ConnectionFactory secondInputRabbitConnectionFactory;
    protected ConnectionFactory outputRabbitConnectionFactory;

    public RabbitConnectionHelper(
            @Value("${rabbitmqUrl}") String rabbitmqUrl,
            @Value("${rabbitmqPort}") Integer rabbitmqPort,
            @Value("${rabbitmqUser}") String rabbitmqUser,
            @Value("${rabbitmqPass}") String rabbitmqPass) {
        this.rabbitmqUrl = rabbitmqUrl;
        this.rabbitmqPort = rabbitmqPort;
        this.rabbitmqUser = rabbitmqUser;
        this.rabbitmqPass = rabbitmqPass;

        inputRabbitConnectionFactory = new ConnectionFactory();
        inputRabbitConnectionFactory.setHost(rabbitmqUrl);
        inputRabbitConnectionFactory.setPort(rabbitmqPort);
        inputRabbitConnectionFactory.setUsername(rabbitmqUser);
        inputRabbitConnectionFactory.setPassword(rabbitmqPass);

        secondInputRabbitConnectionFactory = new ConnectionFactory();
        secondInputRabbitConnectionFactory.setHost(rabbitmqUrl);
        secondInputRabbitConnectionFactory.setPort(rabbitmqPort);
        secondInputRabbitConnectionFactory.setUsername(rabbitmqUser);
        secondInputRabbitConnectionFactory.setPassword(rabbitmqPass);

        outputRabbitConnectionFactory = new ConnectionFactory();
        outputRabbitConnectionFactory.setHost(rabbitmqUrl);
        outputRabbitConnectionFactory.setPort(rabbitmqPort);
        outputRabbitConnectionFactory.setUsername(rabbitmqUser);
        outputRabbitConnectionFactory.setPassword(rabbitmqPass);
    }

    public void publishSystemNameEvent(String eventType, String entityType, String queueName, String payload) throws Exception {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("event", eventType);
        headerMap.put("entity-type", entityType);
        headerMap.put("major-version", 1);
        AMQP.BasicProperties amqpProperties = new AMQP.BasicProperties
                .Builder()
                .headers(headerMap)
                .build();
        try (Connection connection = inputRabbitConnectionFactory.newConnection(); Channel channel = connection.createChannel()) {
            channel.basicPublish("", queueName, amqpProperties, payload.getBytes());
        }
        Thread.sleep(100);
    }

    public void publishSecondSystemNameEvent(String queueName, String payload) throws Exception {
        try (Connection connection = secondInputRabbitConnectionFactory.newConnection(); Channel channel = connection.createChannel()) {
            channel.basicPublish("", queueName, new AMQP.BasicProperties.Builder().build(), payload.getBytes());
        }
        Thread.sleep(100);
    }

    public String getMessageFromQueue(String queueName) throws Exception {
        Thread.sleep(400);
        String body = null;
        try (Connection connection = outputRabbitConnectionFactory.newConnection(); Channel channel = connection.createChannel()) {
            GetResponse response = channel.basicGet(queueName, false);
            if (response == null) {
                // No message retrieved.
            } else {
                AMQP.BasicProperties props = response.getProps();
                body = new String(response.getBody(), StandardCharsets.UTF_8);
                long deliveryTag = response.getEnvelope().getDeliveryTag();
                channel.basicAck(deliveryTag, false);
            }
        }
        return body;
    }
}
