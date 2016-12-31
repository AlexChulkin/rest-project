package com.example.repository;

import com.example.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;

/**
 * Created by alexc_000 on 2016-12-29.
 */
public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByName(String name);

    List<Product> findByTimestamp(Instant timestamp);
}

