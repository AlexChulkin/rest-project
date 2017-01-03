package com.example.endpoint;

import com.example.domain.NameAndPrice;
import com.example.domain.Product;
import com.example.domain.TimestampAndPrice;
import com.example.serialization.xml.DateTimeFieldHandler;
import com.example.service.ProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.example.config.ConfigurationConstants.*;

/**
 * Created by alexc_000 on 2016-12-29.
 */
@RestController
@RequestMapping(value = "/product")
@Validated
@Log4j
public class ProductEndpoint extends AbstractEndpoint {
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ApiOperation(
            value = "Get all products with given name",
            notes = "Returns the products with given name specified by the size parameter with page offset specified by page parameter.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Returns the list of products having the given name"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST,
                    message = "Request mapping should be like /name/{name}. " +
                            "Name must be not-null, not blank string containing not more than 60 symbols." +
                            "Page index should be not-negative integer, page size should be positive integer")
    })
    public ResponseEntity<List<TimestampAndPrice>> findProductsByName(
            @NotBlank(message = "{error.product.name.blank}")
            @Length(max = 60, message = "{error.product.name.lengh}")
            @PathVariable("name") String name,
            @ApiParam("The size of the page to be returned") @Min(value = 1, message = "{error.findProduct.pageSize.notPositive}") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
            @ApiParam("Zero-based page index") @Min(value = 0, message = "{error.findProduct.pageIndex.negative}") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex) {
        log.info(messages.get("productEndpoint.findProductsByName.before", new Object[]{name, pageIndex, pageSize}));
        List<TimestampAndPrice> result = productService.findProductsByName(name, pageIndex, pageSize);

        log.info(messages.get("productEndpoint.findProductsByName.after", new Object[]{name, pageIndex, pageSize}));
        log.info("Successfully found products with product name: " + name + ", page index: " + pageIndex + ", page size: " + pageSize);
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value = "/timestamp/{timestamp}", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ApiOperation(
            value = "Get all products with given timestamp",
            notes = "Returns the products with given timestamp" +
                    " specified by the size parameter with page offset specified by page parameter.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Returns the list of products having the given timestamp"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Request mapping should be like /timestamo/{timestamp}." +
                    "Timestamp must be of format 'yyyy-MM-dd hh:mm:ss'" +
                    "Page index should be not-negative integer, page size should be positive integer")
    })
    public ResponseEntity<List<NameAndPrice>> findProductsByTimestamp(
            @NotBlank(message = "{error.product.timestamp.blank}")
            @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN) @PathVariable("timestamp") String timestampString,
            @ApiParam("The size of the page to be returned") @Min(value = 1, message = "{error.findProduct.pageSize.notPositive}") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
            @ApiParam("Zero-based page index") @Min(value = 0, message = "{error.findProduct.pageIndex.negative}") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex) {

        Instant timestamp = DateTimeFieldHandler.parse(timestampString);
        log.info(messages.get("productEndpoint.findProductsByTimestamp.before", new Object[]{timestampString, pageIndex, pageSize}));
        List<NameAndPrice> result = productService.findProductsByTimestamp(timestamp, pageIndex, pageSize);
        log.info(messages.get("productEndpoint.findProductsByTimestamp.after", new Object[]{timestampString, pageIndex, pageSize}));
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"},
            consumes = {"application/json", "application/xml"})
    @ApiOperation(
            value = "Persists the given product to the database",
            notes = "Persists the given product to the database. Returns the persisted entity",
            response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_CREATED, message = "Returns the newly persisted product entity"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Either the json/xml input entity contains the format errors" +
                    "or the input product entity contains validation errors, or some database error occurred " +
                    "(probably there was a try to violate the DB constraints relating the unique key (name, timestamp) and " +
                    "save operation rollbacked.")
    })
    public ResponseEntity<Product> create(@Valid @RequestBody Product product,
                                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info(messages.get("productEndpoint.createProduct.before", new Object[]{product}));
        Product newProduct = productService.saveProduct(product);
        log.info(messages.get("productEndpoint.createProduct.after", new Object[]{product}));
        return ResponseEntity
                .created(new URI(request.getRequestURL().append("/").append(newProduct.getId()).toString()))
                .body(newProduct)
                ;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = {"application/json", "application/xml"})
    @ApiOperation(
            value = "Updates the given product entity in the database",
            notes = "Updates the given product in the database. Returns NO_CONTENT.",
            response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_NO_CONTENT, message = "Operation succeeded"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Resource(entity) with given id is not found"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Either the json/xml input entity contains " +
                    "the format errors or the input product entity is not validated, or id is null or not a positive long number or not correspondent to given entity," +
                    " or some database error occurred (probably there was a try to violate the DB constraints relating the unique key (name, timestamp) and " +
                    " save operation rollbacked.\"")
    })
    public ResponseEntity<Void> update(@Valid @RequestBody Product product,
                                       @NotNull(message = "{error.product.id.null}") @Min(value = 1L, message = "{error.product.id.min}")
                                       @PathVariable("id") Long id) {
        checkIfIdExists(id);
        checkIdAndEntityForConsistency(id, product);
        log.info(messages.get("productEndpoint.updateProduct.before", new Object[]{product}));
        productService.saveProduct(product);
        log.info(messages.get("productEndpoint.updateProduct.after", new Object[]{product}));
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(
            value = "Deletes the the product with given id in the database",
            response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_NO_CONTENT, message = "Operation succeeded"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Resource(entity) with given id is not found"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Either id is null or not a positive long number")
    })
    public ResponseEntity<Void> delete(@NotNull(message = "{error.product.id.null}")
                                       @Min(value = 1L, message = "{error.product.id.min}") @PathVariable("id") Long id) {
        checkIfIdExists(id);
        log.info(messages.get("productEndpoint.deleteProduct.before", new Object[]{id}));
        Product product = productService.findProductById(id);
        productService.deleteProduct(product);
        log.info(messages.get("productEndpoint.deleteProduct.after", new Object[]{id}));
        return ResponseEntity.noContent().build();
    }

    private void checkIfIdExists(Long id) {
        Product idEntity = productService.findProductById(id);
        Optional.ofNullable(idEntity).orElseThrow(()
                -> new ResourceNotFoundException(messages.get("productEndpoint.resourceNotFound.message",
                new Object[]{id})));
    }

    private void checkIdAndEntityForConsistency(Long id, Product entity) {
        if (!id.equals(entity.getId())) {
            throw new InconsistentEntityAndIdException(messages.get("productEndpoint.inconsistentEntityAndId.message",
                    new Object[]{id}));
        }
    }
}



