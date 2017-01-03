package com.example.endpoint;

import com.example.components.Messages;
import com.example.domain.Product;
import com.example.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

import static com.example.config.ConfigurationConstants.*;
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
@Sql("classpath:/static/schema.sql")
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
    @Autowired
    private Messages messages;

    private Product testProduct;
    private Product team5, team2, tea0, teap2, teap5, teap7, coffeem5, coffeep5, nutsm5, nutsm2, nuts0, cinnamon0, cinnamonp2;

    @Before
    public void setup() throws Exception {
        super.setup();
        productService.saveProduct(createProduct(ICE_CREAM, ONE, NOW_MINUS_5));
        entityManager.flush();
        testProduct = productService.findProductById(1L);
        entityManager.refresh(testProduct);
        team5 = createProduct(TEA, TWO, NOW_MINUS_5);
        team2 = createProduct(TEA, FIVE, NOW_MINUS_2);
        tea0 = createProduct(TEA, TEN, NOW);
        teap2 = createProduct(TEA, ONE, NOW_PLUS_2);
        teap5 = createProduct(TEA, TWO, NOW_PLUS_5);
        teap7 = createProduct(TEA, TEN, NOW_PLUS_7);
        coffeem5 = createProduct(COFFEE, FIVE, NOW_MINUS_5);
        coffeep5 = createProduct(COFFEE, TWO, NOW_PLUS_5);
        nutsm5 = createProduct(NUTS, TEN, NOW_MINUS_5);
        nutsm2 = createProduct(NUTS, FIVE, NOW_MINUS_2);
        nuts0 = createProduct(NUTS, TWO, NOW);
        cinnamon0 = createProduct(CINNAMON, TEN, NOW);
        cinnamonp2 = createProduct(CINNAMON, FIVE, NOW_PLUS_2);


        productService.saveProduct(team5);
        productService.saveProduct(coffeem5);
        productService.saveProduct(nutsm5);
        productService.saveProduct(team2);
        productService.saveProduct(nutsm2);
        productService.saveProduct(tea0);
        productService.saveProduct(cinnamon0);
        productService.saveProduct(nuts0);
        productService.saveProduct(cinnamonp2);
        productService.saveProduct(coffeep5);
        productService.saveProduct(teap2);
        productService.saveProduct(teap5);
        productService.saveProduct(teap7);

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
                .andExpect(jsonPath("$", hasSize(Integer.valueOf(DEFAULT_PAGE_SIZE))))
                .andExpect(jsonPath("$[0].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(team5.getTimestamp()))))
                .andExpect(jsonPath("$[1].price", is(team2.getPrice().toString())))
                .andExpect(jsonPath("$[1].timestamp", is(DATE_TIME_FORMATTER.format(team2.getTimestamp()))))
                .andExpect(jsonPath("$[2].price", is(tea0.getPrice().toString())))
                .andExpect(jsonPath("$[2].timestamp", is(DATE_TIME_FORMATTER.format(tea0.getTimestamp()))))
                .andExpect(jsonPath("$[3].price", is(teap2.getPrice().toString())))
                .andExpect(jsonPath("$[3].timestamp", is(DATE_TIME_FORMATTER.format(teap2.getTimestamp()))))
                .andExpect(jsonPath("$[4].price", is(teap5.getPrice().toString())))
                .andExpect(jsonPath("$[4].timestamp", is(DATE_TIME_FORMATTER.format(teap5.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}?pageIndex=1", TEA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(teap7.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(teap7.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}?pageSize=2", TEA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(team5.getTimestamp()))))
                .andExpect(jsonPath("$[1].price", is(team2.getPrice().toString())))
                .andExpect(jsonPath("$[1].timestamp", is(DATE_TIME_FORMATTER.format(team2.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}?pageSize=2&pageIndex=2", TEA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(teap5.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(teap5.getTimestamp()))))
                .andExpect(jsonPath("$[1].price", is(teap7.getPrice().toString())))
                .andExpect(jsonPath("$[1].timestamp", is(DATE_TIME_FORMATTER.format(teap7.getTimestamp()))))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameNullName() throws Exception {
        mockMvc.perform(get("/product/name/"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/").nodeCount(1))
                .andExpect(xpath("/error/message").string(is(messages.get("error.productEndpointTest.name.null"))))
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
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg0')].message", hasItem(messages.get("error.product.name.blank"))))
                .andReturn()
        ;
    }

    @Test
    public void findProductsByNameTooLongName() throws Exception {
        StringBuilder sb = new StringBuilder();
        IntStream.rangeClosed(0, MAX_NAME_LENGTH).forEach(sb::append);

        mockMvc.perform(get("/product/name/{name}", sb.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg0')].message", hasItem(messages.get("error.product.name.tooLong"))))
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
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg2')].message", hasItem(messages.get("error.productEndpoint.pageIndex.negative"))))
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
                .andExpect(jsonPath("$.message", is(messages.get("error.productEndpoint.pageIndex.notInteger"))))
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
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg1')].message", hasItem(messages.get("error.productEndpoint.pageSize.notPositive"))))
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
                .andExpect(jsonPath("$.message", is(messages.get("error.productEndpoint.pageSize.notInteger"))))
                .andReturn()
        ;
    }

    private Product createProduct(String name, BigDecimal price, Instant timestamp) {
        return new Product(name, timestamp, price);
    }


}
