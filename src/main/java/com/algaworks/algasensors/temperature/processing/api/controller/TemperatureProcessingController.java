package com.algaworks.algasensors.temperature.processing.api.controller;

import com.algaworks.algasensors.temperature.processing.api.model.TemperatureLogOutput;
import com.algaworks.algasensors.temperature.processing.commun.IdGenerator;
import io.hypersistence.tsid.TSID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

import static com.algaworks.algasensors.temperature.processing.intrastructure.rabbitmq.RabbitMQConfig.FANOUT_EXCHANGE_NAME;

@RestController
@RequestMapping("api/sensors/{sensorId}/temperatures/data")
@Slf4j
@AllArgsConstructor
public class TemperatureProcessingController {

    private RabbitTemplate rabbitTemplate;

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public void data(@PathVariable TSID sensorId, @RequestBody String input) {
        if (input == null || input.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Double temperature;

        try {
            temperature = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        TemperatureLogOutput output = TemperatureLogOutput.builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .sensorId(sensorId)
                .value(temperature)
                .registeredAt(OffsetDateTime.now())
                .build();

        log.info(output.toString());

        String exchange = FANOUT_EXCHANGE_NAME;
        String rouding = "";
        Object payload = output;
        MessagePostProcessor messagePostProcessor = message ->{
            message.getMessageProperties().setHeader("sensorId",output.getSensorId().toString());
            return message;
        } ;

        rabbitTemplate.convertAndSend(exchange, rouding, payload, messagePostProcessor);
    }
}
