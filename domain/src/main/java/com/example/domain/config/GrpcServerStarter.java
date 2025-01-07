package com.example.domain.config;

import com.example.domain.domain.ProductEntity;
import com.example.domain.repository.ProductRepository;
import io.grpc.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GrpcServerStarter implements CommandLineRunner {

    private final Server grpcServer;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        var firstProduct = new ProductEntity()
                .setTitle("Apple Macbook Air M2 (2022)")
                .setPrice(999.90)
                .setDescription("Процессор: Apple M2, ОЗУ: 16 гб., Накопитель: 256 гб., Экран: 2560x1440");
        productRepository.save(firstProduct);

        grpcServer.start();
        System.out.println("GRPC-Сервер запущен на порту 50051!");
        grpcServer.awaitTermination();
    }
}