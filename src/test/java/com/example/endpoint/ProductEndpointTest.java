package com.example.endpoint;

import com.example.domain.Product;
import com.example.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.example.RestProjectApplication.DATE_TIME_FORMATTER;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by alexc_000 on 2017-01-02.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@Log4j
@Sql("/static/schema.sql")
public class ProductEndpointTest extends AbstractEndpointTest {
    private static final Instant NOW = Instant.now();
    private static final Instant NOW_PLUS_2 = NOW.plus(2, ChronoUnit.HOURS);
    private static final Instant NOW_PLUS_5 = NOW.plus(5, ChronoUnit.HOURS);
    private static final Instant NOW_PLUS_7 = NOW.plus(7, ChronoUnit.HOURS);
    private static final Instant NOW_MINUS_2 = NOW.minus(2, ChronoUnit.HOURS);
    private static final Instant NOW_MINUS_5 = NOW.minus(5, ChronoUnit.HOURS);
    private static final String NUTS = "Nuts";
    private static final String COFFEE = "Coffee";
    private static final String TEA = "Tea";
    private static final String CINNAMON = "Cinnamon";
    private static final String ICE_CREAM = "Ice cream";
    private static final BigDecimal ONE = BigDecimal.ONE.setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal TWO = BigDecimal.valueOf(2).setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal FIVE = BigDecimal.valueOf(5).setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal TEN = BigDecimal.TEN.setScale(2, BigDecimal.ROUND_HALF_UP);
    @Autowired
    EntityManager entityManager;
    @Autowired
    private ProductService productService;
    private Product testProduct;
    private Product tea1, tea2, tea3, tea4, tea5, tea6;

    @Before
    public void setup() throws Exception {
        super.setup();
        productService.saveProduct(createProduct(ICE_CREAM, ONE, NOW_MINUS_5));
        entityManager.flush();
        testProduct = productService.findProductById(1L);
        entityManager.refresh(testProduct);
        tea1 = createProduct(TEA, TWO, NOW_MINUS_5);
        tea2 = createProduct(TEA, FIVE, NOW_MINUS_2);
        tea3 = createProduct(TEA, TEN, NOW);
        tea4 = createProduct(TEA, ONE, NOW_PLUS_2);
        tea5 = createProduct(TEA, TWO, NOW_PLUS_5);
        tea6 = createProduct(TEA, TEN, NOW_PLUS_7);

        productService.saveProduct(tea1);
        productService.saveProduct(createProduct(COFFEE, FIVE, NOW_MINUS_5));
        productService.saveProduct(createProduct(NUTS, TEN, NOW_MINUS_5));
        productService.saveProduct(tea2);
        productService.saveProduct(createProduct(NUTS, FIVE, NOW_MINUS_2));
        productService.saveProduct(tea3);
        productService.saveProduct(createProduct(CINNAMON, TEN, NOW));
        productService.saveProduct(createProduct(NUTS, TWO, NOW));
        productService.saveProduct(createProduct(CINNAMON, FIVE, NOW_PLUS_2));
        productService.saveProduct(createProduct(COFFEE, TWO, NOW_PLUS_5));
        productService.saveProduct(tea4);
        productService.saveProduct(tea5);
        productService.saveProduct(tea6);

    }

    @Test
    public void findProductsByNamePositive() throws Exception {
        String name = testProduct.getName();

        mockMvc.perform(get("/product/name/{name}", name))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(testProduct.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}", TEA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].price", is(tea1.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(tea1.getTimestamp()))))
                .andExpect(jsonPath("$[1].price", is(tea2.getPrice().toString())))
                .andExpect(jsonPath("$[1].timestamp", is(DATE_TIME_FORMATTER.format(tea2.getTimestamp()))))
                .andExpect(jsonPath("$[2].price", is(tea3.getPrice().toString())))
                .andExpect(jsonPath("$[2].timestamp", is(DATE_TIME_FORMATTER.format(tea3.getTimestamp()))))
                .andExpect(jsonPath("$[3].price", is(tea4.getPrice().toString())))
                .andExpect(jsonPath("$[3].timestamp", is(DATE_TIME_FORMATTER.format(tea4.getTimestamp()))))
                .andExpect(jsonPath("$[4].price", is(tea5.getPrice().toString())))
                .andExpect(jsonPath("$[4].timestamp", is(DATE_TIME_FORMATTER.format(tea5.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}?pageIndex=1", TEA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(tea6.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(tea6.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}?pageSize=2", TEA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(tea1.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(tea1.getTimestamp()))))
                .andExpect(jsonPath("$[1].price", is(tea2.getPrice().toString())))
                .andExpect(jsonPath("$[1].timestamp", is(DATE_TIME_FORMATTER.format(tea2.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}?pageSize=2&pageIndex=2", TEA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(tea5.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(tea5.getTimestamp()))))
                .andExpect(jsonPath("$[1].price", is(tea6.getPrice().toString())))
                .andExpect(jsonPath("$[1].timestamp", is(DATE_TIME_FORMATTER.format(tea6.getTimestamp()))))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameNullName() throws Exception {
        mockMvc.perform(get("/product/name/{name}", null))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
//                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(testProduct.getTimestamp()))))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameBlankName() throws Exception {
        mockMvc.perform(get("/product/name/{name}", "   "))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg0')].message", hasItem("Product name can't be blank")))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameTooLongName() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 61; i++) {
            sb.append("s");
        }
        mockMvc.perform(get("/product/name/{name}", sb.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg0')].message", hasItem("Product name can't exceed 60 symbols")))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameNegativePageIndex() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageIndex=-1", testProduct.getName()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg2')].message", hasItem("Page index must be non-negative integer")))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameNotIntegerPageIndex() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageIndex=t", testProduct.getName()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.message", is("Failed to convert value of " +
                        "type 'java.lang.String' to required type 'java.lang.Integer'; " +
                        "nested exception is java.lang.NumberFormatException: For input string: \"t\"")))
                .andReturn()
        ;
    }


    @Test
    public void findProductsByNameNotPositivePageSize() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageSize=0", testProduct.getName()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg1')].message", hasItem("Page size must be positive integer")))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameNotIntegerPageSize() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageSize=t", testProduct.getName()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.message", is("Failed to convert value of " +
                        "type 'java.lang.String' to required type 'java.lang.Integer'; " +
                        "nested exception is java.lang.NumberFormatException: For input string: \"t\"")))
                .andReturn()
        ;
    }

    private Product createProduct(String name, BigDecimal price, Instant timestamp) {
        return new Product(name, timestamp, price);
    }


}
