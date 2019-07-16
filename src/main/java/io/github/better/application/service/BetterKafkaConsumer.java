package io.github.better.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BetterKafkaConsumer {

    private final Logger log = LoggerFactory.getLogger(BetterKafkaConsumer.class);
    private static final String TOPIC = "topic_better";

    @KafkaListener(topics = "topic_better", groupId = "group_id")
    public void consume(String message) throws IOException {
        log.info("Consumed message in {} : {}", TOPIC, message);
    }
}
