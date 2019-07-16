package io.github.better.application.web.rest;

import io.github.better.application.service.BetterKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/better-kafka")
public class BetterKafkaResource {

    private final Logger log = LoggerFactory.getLogger(BetterKafkaResource.class);

    private BetterKafkaProducer kafkaProducer;

    public BetterKafkaResource(BetterKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
        log.debug("REST request to send to Kafka topic the message : {}", message);
        this.kafkaProducer.sendMessage(message);
    }
}
