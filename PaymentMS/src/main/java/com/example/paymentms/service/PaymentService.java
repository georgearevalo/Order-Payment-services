package com.example.paymentms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio para procesar pagos.
 * Este servicio escucha los mensajes del tema 'order-placed' de Kafka,
 * simula el procesamiento de un pago y envía el resultado a otro tema de Kafka.
 */
@Service
public class PaymentService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Consume un mensaje del tema 'order-placed' de Kafka, que contiene el ID de un pedido.
     * Simula el procesamiento del pago (con un 80% de probabilidad de éxito) y
     * produce un mensaje en el tema 'payment-processed' con el resultado del pago.
     * El formato del mensaje de salida es "orderId:status", donde el estado puede ser "SUCCESS" o "FAILURE".
     *
     * @param orderId El ID del pedido a procesar, recibido del tema de Kafka.
     */
    @KafkaListener(topics = "order-placed", groupId = "payment-group")
    public void consume(String orderId) {
        // Simulate payment processing
        System.out.println("Processing payment for order: " + orderId);

        // Simulate a random success or failure
        boolean paymentSuccess = Math.random() > 0.2; // 80% success rate

        String status = paymentSuccess ? "SUCCESS" : "FAILURE";

        // Produce a message to the payment-processed topic
        String message = orderId + ":" + status;
        kafkaTemplate.send("payment-processed", message);
    }
}
