package com.example.domain.rabbitmq;

import com.example.domain.domain.ProductEntity;
import com.example.domain.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventListener {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;


    @RabbitListener(queues = "createdProducts")
    public void listenCreatedProducts(String message) {
        ProductEntity product;
        try {
            System.out.println(message);
            product = objectMapper.readValue(message, ProductEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        productRepository.save(product);
    }

    @RabbitListener(queues = "updatedProducts")
    public void listenUpdatedProducts(String message) {
        ProductEntity product;
        try {
            System.out.println(message);
            product = objectMapper.readValue(message, ProductEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var updatedProduct = productRepository.findById(product.getId())
                .orElse(null);
        assert updatedProduct != null;

        updatedProduct.setTitle(product.getTitle());
        updatedProduct.setPrice(product.getPrice());
        updatedProduct.setDescription(product.getDescription());

        productRepository.save(updatedProduct);
    }

    @RabbitListener(queues = "deletedProducts")
    public void listenDeletedProducts(String message) throws JsonProcessingException {
        ProductEntity product;
        try {
            System.out.println(message);
            product = objectMapper.readValue(message, ProductEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        productRepository.deleteById(product.getId());
    }
}
