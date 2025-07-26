package com.algaworks.algasensors.temperature.processing.intrastructure.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMQInitialize {

    private RabbitAdmin rabbitAdmin;

    @PostConstruct
    public void init() {
        rabbitAdmin.initialize();
    }
}
