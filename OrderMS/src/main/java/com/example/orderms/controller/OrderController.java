package com.example.orderms.controller;

import com.example.orderms.model.Order;
import com.example.orderms.repository.OrderRepository;
import com.example.orderms.service.KafkaProducerService;
import com.example.orderms.service.RsaEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador REST para gestionar las órdenes.
 * Proporciona endpoints para crear y obtener órdenes.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

    /**
     * Crea una nueva orden.
     * Cifra los datos de la tarjeta, guarda la orden en la base de datos con estado
     * 'PENDIENTE'
     * y envía un mensaje a Kafka para indicar que se ha realizado una orden.
     *
     * @param orderRequest La solicitud de la orden que contiene la información del
     *                     producto y los datos de la tarjeta.
     * @return La orden creada con su ID y estado.
     * @throws Exception Si ocurre un error durante el cifrado de los datos de la
     *                   tarjeta.
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) throws Exception {
        Order order = new Order();
        order.setProductInfo(orderRequest.getProductInfo());
        order.setStatus("PENDIENTE");

        String encryptedCardData = rsaEncryptionService.encrypt(orderRequest.getCardData());
        order.setCardData(encryptedCardData);

        Order savedOrder = orderRepository.save(order);

        kafkaProducerService.sendMessage("order-placed", savedOrder.getId().toString());

        return ResponseEntity.ok(savedOrder);
    }

    /**
     * Obtiene una orden por su ID.
     *
     * @param id El ID de la orden a obtener.
     * @return La orden si se encuentra, o una respuesta 'not found' si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
