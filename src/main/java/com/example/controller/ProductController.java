package com.example.controller;

import com.example.domain.NameAndPrice;
import com.example.domain.Product;
import com.example.domain.TimestampAndPrice;
import com.example.handler.ControllerValidationHandler;
import com.example.handler.DateTimeFieldHandler;
import com.example.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

/**
 * Created by alexc_000 on 2016-12-29.
 */
@Controller
@RequestMapping(value = "/product")
@Validated
@Log4j
public class ProductController extends ControllerValidationHandler {

    @Autowired
    private ProductService productService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/pn/{pn}", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ResponseBody
    public List<TimestampAndPrice> findProductsByName(@Size(min = 1, max = 60) @PathVariable("pn") String name) {
        log.info("Finding products with product name: " + name);
        List<TimestampAndPrice> result = productService.findProductsByName(name);

        log.info("Successfully found products with product name: " + name);
        return result;
    }

    @RequestMapping(value = "/ts/{ts}", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})

    @ResponseBody
    public List<NameAndPrice> findProductsByTimestamp(
            @NotNull @DateTimeFormat(pattern = DateTimeFieldHandler.dateFormatPattern) @PathVariable("ts") String timestampString) {
        Instant timestamp = DateTimeFieldHandler.parse(timestampString);
        log.info("Finding products with timestamp: " + timestampString);
        List<NameAndPrice> result = productService.findProductsByTimestamp(timestamp);
        log.info("Successfully found products with timestamp: " + timestampString);
        return result;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"},
            consumes = {"application/json", "application/xml"})
    @ResponseBody
    public Product create(@Valid @RequestBody Product product) {
        log.info("Creating product: " + product);
        productService.saveProduct(product);
        log.info("Successfully created product: " + product);
        return product;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = {"application/json", "application/xml"})
    @ResponseBody
    public void update(@Valid @RequestBody Product product,
                       @NotNull @PathVariable("id") Long id) {
        checkIfIdExists(id);
        checkIdAndEntityForConsistency(id, product);
        log.info("Updating product: " + product);
        productService.saveProduct(product);
        log.info("Product updated successfully with info: " + product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@NotNull @PathVariable("id") Long id) {
        checkIfIdExists(id);
        log.info("Deleting product with id: " + id);
        Product product = productService.findProductById(id);
        productService.deleteProduct(product);
        log.info("Product deleted successfully");
    }

    @Override
    protected ProductService getService() {
        return productService;
    }
}



