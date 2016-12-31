package com.example.controller;

import com.example.domain.Product;
import com.example.domain.Products;
import com.example.handler.ControllerValidationHandler;
import com.example.handler.DateTimeFieldHandler;
import com.example.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Created by alexc_000 on 2016-12-29.
 */
@Controller
@RequestMapping(value = "/product")
@Validated
@Log4j
public class ProductController extends ControllerValidationHandler {
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/pn/{pn}", method = RequestMethod.GET)
    @ResponseBody
    public Products findProductsByProductName(@NotBlank @PathVariable("pn") String productName) {
        logger.info("Finding products with product name: " + productName);
        Products result = new Products().setTimestampAndPriceList(productService.findProductsByProductName(productName));
        logger.info("Successfully found products with product name: " + productName);
        return result;
    }

    @RequestMapping(value = "/ts/{ts}", method = RequestMethod.GET)
    @ResponseBody
    public Products findProductsByTimestamp(
            @NotNull @DateTimeFormat(pattern = DateTimeFieldHandler.dateFormatPattern) @PathVariable("ts") String timestampString) {
        Instant timestamp = DateTimeFieldHandler.parse(timestampString);
        logger.info("Finding products with timestamp: " + timestampString);
        Products result = new Products().setNameAndPriceList(productService.findProductsByTimestamp(timestamp));
        logger.info("Successfully found products with timestamp: " + timestampString);
        return result;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Product create(@Valid @RequestBody Product product) {
        logger.info("Creating product: " + product);
        productService.saveProduct(product);
        logger.info("Successfully created product: " + product);
        return product;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void update(@Valid @RequestBody Product product,
                       @NotNull @PathVariable("id") Long id) {
        checkIfIdExists(id);
        checkIdAndEntityForConsistency(id, product);
        logger.info("Updating product: " + product);
        productService.saveProduct(product);
        logger.info("Product updated successfully with info: " + product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@NotNull @PathVariable("id") Long id) {
        checkIfIdExists(id);
        logger.info("Deleting product with id: " + id);
        Product product = productService.findProductById(id);
        productService.deleteProduct(product);
        logger.info("Product deleted successfully");
    }

    @Override
    protected ProductService getService() {
        return productService;
    }
}



