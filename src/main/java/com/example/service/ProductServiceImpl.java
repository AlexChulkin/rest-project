package com.example.service;

import com.example.domain.NameAndPrice;
import com.example.domain.Product;
import com.example.domain.TimestampAndPrice;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexc_000 on 2016-12-29.
 */
@Service("productService")
@Repository
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private static final String INSTANT_TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(INSTANT_TIMESTAMP_FORMAT);

    @Autowired
    private ProductRepository productRepository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<TimestampAndPrice> findProductsByName(String name, Integer pageIndex, Integer pageSize) {
        return Collections.unmodifiableList(em.createNamedQuery("Product.findProductsByName", TimestampAndPrice.class)
                .setParameter("name", name)
                .setFirstResult(pageIndex * pageSize)
                .setMaxResults(pageSize)
                .getResultList());
    }

    @Override
    public List<NameAndPrice> findProductsByTimestamp(Instant timestamp, Integer pageIndex, Integer pageSize) {
        return Collections.unmodifiableList(em.createNamedQuery("Product.findProductsByTimestamp", NameAndPrice.class)
                .setParameter("timestamp", DATE_TIME_FORMATTER.format(timestamp))
                .setFirstResult(pageIndex * pageSize)
                .setMaxResults(pageSize)
                .getResultList());
    }

    @Override
    public Product findProductById(Long productId) {
        return productRepository.findOne(productId);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }
}
