package com.example.orderms.service;

import com.example.orderms.model.Order;
import com.example.orderms.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumerService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Consume un mensaje de Kafka del tema 'payment-processed' y actualiza el estado del pedido.
     * El formato del mensaje esperado es "orderId:status".
     * Si el estado es "SUCCESS", el estado del pedido se actualiza a "PAGADO".
     * En caso contrario, se actualiza a "FALLO_PAGO".
     *
     * @param message El mensaje recibido del tema de Kafka.
     */
    @KafkaListener(topics = "payment-processed", groupId = "order-group")
    public void consume(String message) {
        // message format: "orderId:status"
        String[] parts = message.split(":");
        Long orderId = Long.parseLong(parts[0]);
        String status = parts[1];

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if ("SUCCESS".equals(status)) {
                order.setStatus("PAGADO");
            } else {
                order.setStatus("FALLO_PAGO");
            }
            orderRepository.save(order);
        }
    }
}
