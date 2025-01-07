package com.example.gateway.rest;

import com.example.gateway.redis.RedisOperations;
import com.example.gateway.rest.dto.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import product.ProductOuterClass;
import product.ProductServiceGrpc;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductRestController {

    @Value("${grpc.server.host}")
    private String grpcHost;
    @Value("${grpc.server.port}")
    private int grpcPort;

    private ManagedChannel channel;
    private ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    private final RedisOperations<ProductDto> redisOperations;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;


    @PostConstruct
    private void init() {
        this.channel = ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
        this.blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }


    @GetMapping("/product/all")
    public List<ProductDto> getAll() {
        log.info("GET-запрос для получения всех товаров");
        List<ProductDto> products;
        List<ProductDto> cachedProducts = redisOperations.get("products::all");

        if (cachedProducts.isEmpty()) {
            ProductOuterClass.Empty request = ProductOuterClass.Empty.newBuilder()
                    .build();
            products = blockingStub.getAllProducts(request)
                    .getProductsList()
                    .stream()
                    .map(this::mapToProductDto)
                    .toList();
        } else {
            return cachedProducts;
        }
        redisOperations.save("products::all", products);

        return products;
    }

    @GetMapping("/product/id/{id}")
    public ProductDto get(@PathVariable Long id) {
        log.info("GET-запрос для получения товара с ID: {}", id);
        ProductOuterClass.ProductRequest request = ProductOuterClass.ProductRequest.newBuilder()
                .setId(id)
                .build();

        return mapToProductDto(blockingStub.getProductById(request));
    }

    @PostMapping("/product/add")
    public void add(@RequestBody ProductDto productDTO) {
        log.info("POST-запрос для добавления товара - {}", productDTO);

        try {
            String addedProduct = objectMapper.writeValueAsString(productDTO);
            rabbitTemplate.convertAndSend("createdProducts", addedProduct);
            redisOperations.delete("products::all");
        } catch (JsonProcessingException e) {
            log.error("Ошибка при записи JSON в String!");
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/product/update")
    public void update(@RequestBody ProductDto productDTO) {
        log.info("PUT-запрос для обновления данных товара - {}", productDTO);

        try {
            String updatedProduct = objectMapper.writeValueAsString(productDTO);
            rabbitTemplate.convertAndSend("updatedProducts", updatedProduct);
            redisOperations.delete("products::all");
        } catch (JsonProcessingException e) {
            log.error("Ошибка при записи JSON в String!");
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/product/id/{id}")
    public void deleteBook(@PathVariable Long id) {
        log.info("DELETE-запрос для удаления товара с ID: {}", id);
        rabbitTemplate.convertAndSend("deletedProducts", id);
        redisOperations.delete("products::all");
    }

    private ProductDto mapToProductDto(ProductOuterClass.Product product) {
        return new ProductDto().setId(product.getId())
                .setTitle(product.getTitle())
                .setPrice(product.getPrice())
                .setDescription(product.getDescription());
    }
}
