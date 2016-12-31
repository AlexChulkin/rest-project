package com.example.service;

/**
 * Created by alexc_000 on 2016-12-29.
 */

import com.example.domain.Product;

import java.time.Instant;
import java.util.List;

public interface ProductService {
    List<Product> findProductsByName(String name);

    List<Product> findProductsByTimestamp(Instant timestamp);

    Product findProductById(Long productId);

    Product saveProduct(Product product);

    void deleteProduct(Product product);
}

