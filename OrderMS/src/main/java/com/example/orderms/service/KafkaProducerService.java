package com.example.orderms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Envía un mensaje a un tema de Kafka específico.
     *
     * @param topic   El tema de Kafka al que se enviará el mensaje.
     * @param message El mensaje que se enviará.
     */
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
