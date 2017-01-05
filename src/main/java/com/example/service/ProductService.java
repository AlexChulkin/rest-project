package com.example.service;

/**
 * Created by alexc_000 on 2016-12-29.
 */

import com.example.domain.NameAndPrice;
import com.example.domain.Product;
import com.example.domain.TimestampAndPrice;

import java.time.Instant;
import java.util.List;

public interface ProductService {
    List<TimestampAndPrice> findProductsByName(String name, Integer pageIndex, Integer pageSize);

    List<NameAndPrice> findProductsByTimestamp(Instant timestamp, Integer pageIndex, Integer pageSize);

    Product findProductById(Long productId);

    Product saveProduct(Product product);

    void deleteProduct(Product product);

    void delete(Long id);

    Long findNumberOfProductsWithGivenNameAndTimestamp(String name, Instant timestamp);

    void deleteAll();

    int updateProduct(Product product);
}

