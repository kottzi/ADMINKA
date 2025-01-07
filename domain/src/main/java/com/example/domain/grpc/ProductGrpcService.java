package com.example.domain.grpc;

import com.example.domain.domain.ProductEntity;
import com.example.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import product.ProductOuterClass;
import product.ProductServiceGrpc;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductRepository productRepository;

    @Override
    public void getProductById(
            ProductOuterClass.ProductRequest request,
            io.grpc.stub.StreamObserver<ProductOuterClass.Product> responseObserver
    ) {
        var product = productRepository.findById(request.getId())
                .map(this::mapToProductGrpc)
                .orElse(ProductOuterClass.Product.newBuilder().build());

        responseObserver.onNext(product);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllProducts(
            ProductOuterClass.Empty request,
            io.grpc.stub.StreamObserver<ProductOuterClass.ProductList> responseObserver
    ) {
        var products = productRepository.findAll()
                .stream()
                .map(this::mapToProductGrpc)
                .toList();

        ProductOuterClass.ProductList productList = ProductOuterClass.ProductList.newBuilder()
                .addAllProducts(products)
                .build();

        responseObserver.onNext(productList);
        responseObserver.onCompleted();
    }

    private ProductOuterClass.Product mapToProductGrpc(ProductEntity product) {
        return ProductOuterClass.Product.newBuilder()
                .setId(product.getId() != null ? product.getId() : 0)
                .setTitle(product.getTitle() != null ? product.getTitle() : "")
                .setPrice(product.getPrice() != null ? product.getPrice() : 0.0)
                .setDescription(product.getDescription() != null ? product.getDescription() : "")
                .build();
    }
}