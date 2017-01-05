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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

import static com.example.config.ConfigurationConstants.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private static final String COLA = "Cola";
    private static final BigDecimal ONE = BigDecimal.ONE.setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal TWO = BigDecimal.valueOf(2).setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal FIVE = BigDecimal.valueOf(5).setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal TEN = BigDecimal.TEN.setScale(2, BigDecimal.ROUND_HALF_UP);
    private static final String RESOURCE_LOCATION_PATTERN = "http://localhost/product/*";
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProductService productService;
    @Autowired
    private Messages messages;
    private Product testProduct;
    private String testProductName;
    private String testTimestampString;

    private Product team5, team2, tea0, teap2, teap5, teap7, coffeem5, coffeep5, nutsm5, nutsm2, nuts0,
            cinnamon0, cinnamonp2, cinnamonm5, colam5;

    {
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
        cinnamonm5 = createProduct(CINNAMON, TEN, NOW_MINUS_5);
        cinnamonp2 = createProduct(CINNAMON, FIVE, NOW_PLUS_2);
        colam5 = createProduct(COLA, ONE, NOW_MINUS_5);
    }

    @Before
    public void setup() throws Exception {
        super.setup();

        testProduct = productService.saveProduct(createProduct(ICE_CREAM, ONE, NOW_MINUS_5));
        entityManager.refresh(testProduct);
        testProductName = testProduct.getName();
        testTimestampString = DATE_TIME_FORMATTER.format(testProduct.getTimestamp());
    }

    @Test
    @DirtiesContext
    public void findProductsByNamePositive() throws Exception {
        saveNumerousProducts();
        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
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
    @DirtiesContext
    public void findProductsByNameNullName() throws Exception {
        mockMvc.perform(get("/product/name/"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/").nodeCount(1))
                .andExpect(xpath("/error/message").string(is(messages.get("abstractEndpoint.improper.get.requestmapping"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
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
    @DirtiesContext
    public void findProductsByNameTooLongName() throws Exception {
        StringBuilder sb = new StringBuilder();
        IntStream.rangeClosed(0, MAX_NAME_LENGTH).forEach(sb::append);

        mockMvc.perform(get("/product/name/{name}", sb.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg0')].message", hasItem(messages.get("error.product.name.length"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByNameNegativePageIndex() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageIndex=-1", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg2')].message", hasItem(messages.get("error.productEndpoint.pageIndex.negative"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByNameNotIntegerPageIndex() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageIndex=t", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.message", is(messages.get("error.productEndpoint.notInteger.message"))))
                .andReturn()
        ;
    }


    @Test
    @DirtiesContext
    public void findProductsByNameNotPositivePageSize() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageSize=0", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg1')].message", hasItem(messages.get("error.productEndpoint.pageSize.notPositive"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByNameNotIntegerPageSize() throws Exception {
        mockMvc.perform(get("/product/name/{name}?pageSize=t", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.message", is(messages.get("error.productEndpoint.notInteger.message"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampPositive() throws Exception {
        saveNumerousProducts();
        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(Integer.valueOf(DEFAULT_PAGE_SIZE))))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(team5.getName())))
                .andExpect(jsonPath("$[2].price", is(coffeem5.getPrice().toString())))
                .andExpect(jsonPath("$[2].name", is(coffeem5.getName())))
                .andExpect(jsonPath("$[3].price", is(nutsm5.getPrice().toString())))
                .andExpect(jsonPath("$[3].name", is(nutsm5.getName())))
                .andExpect(jsonPath("$[4].price", is(cinnamonm5.getPrice().toString())))
                .andExpect(jsonPath("$[4].name", is(cinnamonm5.getName())))
                .andReturn()
        ;


        mockMvc.perform(get("/product/timestamp/{timestamp}?pageIndex=1", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(colam5.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(colam5.getName())))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}?pageSize=2", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(team5.getName())))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}?pageSize=2&pageIndex=2", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(cinnamonm5.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(cinnamonm5.getName())))
                .andExpect(jsonPath("$[1].price", is(colam5.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(colam5.getName())))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampNullTimestamp() throws Exception {
        mockMvc.perform(get("/product/timestamp/"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/").nodeCount(1))
                .andExpect(xpath("/error/message").string(is(messages.get("abstractEndpoint.improper.get.requestmapping"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampBlankTimestamp() throws Exception {
        mockMvc.perform(get("/product/timestamp/{timestamp}", "   "))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.abstractEndpoint.DateTimeException.message"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampImproperDateTimeFormat() throws Exception {
        mockMvc.perform(get("/product/timestamp/{timestamp}", "11-11-1999 12:00:00"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.abstractEndpoint.DateTimeException.message"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampNegativePageIndex() throws Exception {
        mockMvc.perform(get("/product/timestamp/{timestamp}?pageIndex=-1", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByTimestamp.arg2')].message", hasItem(messages.get("error.productEndpoint.pageIndex.negative"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampNotIntegerPageIndex() throws Exception {
        mockMvc.perform(get("/product/timestamp/{timestamp}?pageIndex=t", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.message", is(messages.get("error.productEndpoint.notInteger.message"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampNotPositivePageSize() throws Exception {
        mockMvc.perform(get("/product/timestamp/{timestamp}?pageSize=0", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByTimestamp.arg1')].message", hasItem(messages.get("error.productEndpoint.pageSize.notPositive"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void findProductsByTimestampNotIntegerPageSize() throws Exception {
        mockMvc.perform(get("/product/timestamp/{timestamp}?pageSize=t", testProductName))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.message", is(messages.get("error.productEndpoint.notInteger.message"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductPositive() throws Exception {
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern(RESOURCE_LOCATION_PATTERN))
                .andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());
        assert id == 2;
        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(team5.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(team5.getName())))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductWithNullPrice() throws Exception {
        team5.setPrice(null);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'price')].message", hasItem(messages.get("error.product.price.invalid"))))
                .andReturn();
        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductWithPriceLesserThanMin() throws Exception {
        team5.setPrice(BigDecimal.valueOf(0.00));
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'price')].message", hasItem(messages.get("error.product.price.invalid"))))
                .andReturn();
        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductWitPriceBiggerThanMax() throws Exception {
        team5.setPrice(BigDecimal.valueOf(10_000_000_000.00));
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'price')].message", hasItem(messages.get("error.product.price.invalid"))))
                .andReturn();
        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductWithUncorrectTimestampFormat() throws Exception {
        team5.setTimestamp(null);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'timestamp')].message", hasItem(messages.get("error.product.timestamp.null"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductWithBlankName() throws Exception {
        team5.setName("  ");
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'name')].message", hasItem(messages.get("error.product.name.blank"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductWithNullName() throws Exception {
        team5.setName(null);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'name')].message", hasItem(messages.get("error.product.name.blank"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductWithTooLongName() throws Exception {
        StringBuilder sb = new StringBuilder();
        IntStream.rangeClosed(0, MAX_NAME_LENGTH).forEach(sb::append);
        String tooLongName = sb.toString();
        team5.setName(tooLongName);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(post("/product")
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'name')].message", hasItem(messages.get("error.product.name.length"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}", sb.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg0')].message", hasItem(messages.get("error.product.name.length"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductThatViolatesDBConstraints() throws Exception {
        Instant violatingTimestamp = testProduct.getTimestamp();
        String violatingProductName = testProductName;
        Product violatingProduct = new Product(violatingProductName, violatingTimestamp, testProduct.getPrice().add(ONE));
        String violatingProductJson = json(violatingProduct);

        MvcResult result = mockMvc.perform(post("/product")
                .content(violatingProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.productEndpoint.dBConstraintViolation.message", new Object[]{violatingProductName, DATE_TIME_FORMATTER.format(violatingTimestamp)}))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(violatingTimestamp)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}", violatingProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(violatingTimestamp))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void createProductUsingNotValidJson() throws Exception {

        String team5FakeJson = json(team5).substring(1);
        MvcResult result = mockMvc.perform(post("/product")
                .content(team5FakeJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.abstractEndpoint.HttpMessageNotReadableException.message"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductPositive() throws Exception {
        BigDecimal updatedPrice = testProduct.getPrice().add(TEN);
        testProduct.setPrice(updatedPrice);
        String testProductJson = json(testProduct);

        MvcResult result = mockMvc.perform(put("/product/{id}", testProduct.getId())
                .content(testProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(updatedPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(updatedPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductNullId() throws Exception {

        BigDecimal oldPrice = testProduct.getPrice();
        BigDecimal updatedPrice = oldPrice.add(TEN);
        testProduct.setPrice(updatedPrice);
        String testProductJson = json(testProduct);

        MvcResult result = mockMvc.perform(put("/product/")
                .content(testProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem((messages.get("abstractEndpoint.improper.put.requestmapping")))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductBlankId() throws Exception {

        BigDecimal oldPrice = testProduct.getPrice();
        BigDecimal updatedPrice = oldPrice.add(TEN);
        testProduct.setPrice(updatedPrice);
        String testProductJson = json(testProduct);

        MvcResult result = mockMvc.perform(put("/product/{id}", "   ")
                .content(testProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.value == null)].message", hasItem((messages.get("error.product.id.null")))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductNotExistingId() throws Exception {

        BigDecimal oldPrice = testProduct.getPrice();
        BigDecimal updatedPrice = oldPrice.add(TEN);
        testProduct.setPrice(updatedPrice);
        String testProductJson = json(testProduct);
        Long notExistingId = 10000L;
        entityManager.clear();

        MvcResult result = mockMvc.perform(put("/product/{id}", notExistingId)
                .content(testProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.value == null)].message", hasItem((messages.get("error.productEndpoint.resourceNotFound.message", new Object[]{notExistingId})))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductNegativeIdValue() throws Exception {

        BigDecimal oldPrice = testProduct.getPrice();
        BigDecimal updatedPrice = oldPrice.add(TEN);
        testProduct.setPrice(updatedPrice);
        String testProductJson = json(testProduct);

        Long negativeId = -1L;

        MvcResult result = mockMvc.perform(put("/product/{id}", negativeId)
                .content(testProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'update.arg1')].message", hasItem((messages.get("error.product.id.notPositive")))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductNotConsistentResourceAndId() throws Exception {
        productService.saveProduct(team5);
        entityManager.clear();

        BigDecimal oldPrice = testProduct.getPrice();
        BigDecimal updatedPrice = oldPrice.add(TEN);
        testProduct.setPrice(updatedPrice);
        String testProductJson = json(testProduct);

        Long inconsistentId = team5.getId();

        MvcResult result = mockMvc.perform(put("/product/{id}", inconsistentId)
                .content(testProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem((messages.get("error.productEndpoint.inconsistentResourceAndId.message", new Object[]{inconsistentId, testProduct.getId()})))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(team5.getName())))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductNotLongId() throws Exception {

        BigDecimal oldPrice = testProduct.getPrice();
        BigDecimal updatedPrice = oldPrice.add(TEN);
        testProduct.setPrice(updatedPrice);
        String testProductJson = json(testProduct);

        String notLongId = "notLongId";

        MvcResult result = mockMvc.perform(put("/product/{id}", notLongId)
                .content(testProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.productEndpoint.notLong.message"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductUsingNotValidJson() throws Exception {
        BigDecimal oldPrice = testProduct.getPrice();
        testProduct.setPrice(testProduct.getPrice().add(TEN));
        String fakeTestProductJson = json(testProduct).substring(1);
        MvcResult result = mockMvc.perform(put("/product/{id}", testProduct.getId())
                .content(fakeTestProductJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.abstractEndpoint.HttpMessageNotReadableException.message"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", testProductName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(testTimestampString)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", testTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductThatViolatesDBConstraints() throws Exception {
        Instant dangerousTimestamp = testProduct.getTimestamp();
        String dangerousName = testProductName;
        String yetNotViolatingName = COLA;
        Product dangerousProduct = new Product(yetNotViolatingName, dangerousTimestamp, testProduct.getPrice().add(ONE));
        productService.saveProduct(dangerousProduct);
        entityManager.clear();
        dangerousProduct.setName(dangerousName);

        String dangerousJson = json(dangerousProduct);

        MvcResult result = mockMvc.perform(put("/product/{id}", dangerousProduct.getId())
                .content(dangerousJson)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.productEndpoint.dBConstraintViolation.message", new Object[]{dangerousProduct.getName(), DATE_TIME_FORMATTER.format(dangerousTimestamp)}))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(dangerousTimestamp)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(dangerousProduct.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(yetNotViolatingName)))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}", dangerousName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(dangerousTimestamp))))
                .andReturn()
        ;
    }


    @Test
    @DirtiesContext
    public void updateProductWithNullPrice() throws Exception {
        productService.saveProduct(team5);
        entityManager.clear();
        BigDecimal oldPrice = team5.getPrice();

        team5.setPrice(null);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(put("/product/{id}", team5.getId())
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'price')].message", hasItem(messages.get("error.product.price.invalid"))))
                .andReturn();
        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(team5.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[1].name", is(team5.getName())))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductWithPriceLesserThanMin() throws Exception {

        productService.saveProduct(team5);
        entityManager.clear();
        BigDecimal oldPrice = team5.getPrice();

        team5.setPrice(BigDecimal.valueOf(0.00));
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(put("/product/{id}", team5.getId())
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'price')].message", hasItem(messages.get("error.product.price.invalid"))))
                .andReturn();
        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(team5.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[1].name", is(team5.getName())))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductWitPriceBiggerThanMax() throws Exception {
        BigDecimal oldPrice = team5.getPrice();
        productService.saveProduct(team5);
        entityManager.clear();

        team5.setPrice(BigDecimal.valueOf(10_000_000_000.00));
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(put("/product/{id}", team5.getId())
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'price')].message", hasItem(messages.get("error.product.price.invalid"))))
                .andReturn();
        entityManager.clear();


        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(team5.getTimestamp()))))
                .andReturn()
        ;

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(oldPrice.toString())))
                .andExpect(jsonPath("$[1].name", is(team5.getName())))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductWithBlankName() throws Exception {
        String oldName = team5.getName();
        productService.saveProduct(team5);
        entityManager.clear();

        team5.setName("  ");
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(put("/product/{id}", team5.getId())
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'name')].message", hasItem(messages.get("error.product.name.blank"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(oldName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductWithNullName() throws Exception {
        String oldName = team5.getName();
        productService.saveProduct(team5);
        entityManager.clear();

        team5.setName(null);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(put("/product/{id}", team5.getId())
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'name')].message", hasItem(messages.get("error.product.name.blank"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[1].name", is(oldName)))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductWithTooLongName() throws Exception {
        productService.saveProduct(team5);
        entityManager.clear();
        String oldName = team5.getName();


        StringBuilder sb = new StringBuilder();
        IntStream.rangeClosed(0, MAX_NAME_LENGTH).forEach(sb::append);
        String tooLongName = sb.toString();
        team5.setName(tooLongName);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(put("/product/{id}", team5.getId())
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'name')].message", hasItem(messages.get("error.product.name.length"))))
                .andReturn();
        entityManager.clear();

        mockMvc.perform(get("/product/timestamp/{timestamp}", DATE_TIME_FORMATTER.format(team5.getTimestamp())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(testProduct.getPrice().toString())))
                .andExpect(jsonPath("$[0].name", is(testProductName)))
                .andExpect(jsonPath("$[1].name", is(oldName)))
                .andExpect(jsonPath("$[1].price", is(team5.getPrice().toString())))
                .andReturn()
        ;

        mockMvc.perform(get("/product/name/{name}", sb.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'findProductsByName.arg0')].message", hasItem(messages.get("error.product.name.length"))))
                .andReturn()
        ;
    }

    @Test
    @DirtiesContext
    public void updateProductWithUncorrectTimestampFormat() throws Exception {
        productService.saveProduct(team5);
        entityManager.clear();
        Instant oldTimestamp = team5.getTimestamp();
        team5.setTimestamp(null);
        String team5json = json(team5);

        MvcResult result = mockMvc.perform(put("/product/{id}", team5.getId())
                .content(team5json)
                .contentType(JSON_MEDIA_TYPE)
                .accept(JSON_MEDIA_TYPE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'timestamp')].message", hasItem(messages.get("error.product.timestamp.null"))))
                .andReturn();

        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", team5.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(team5.getPrice().toString())))
                .andExpect(jsonPath("$[0].timestamp", is(DATE_TIME_FORMATTER.format(oldTimestamp))))
                .andReturn()
        ;
    }


    @Test
    @DirtiesContext
    public void deleteNullId() throws Exception {
        String oldName = testProductName;
        String oldTimestampString = testTimestampString;
        String oldPriceString = testProduct.getPrice().toString();

        Long nullId = null;

        mockMvc.perform(delete("/product/{id}", nullId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", oldName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].timestamp", is(oldTimestampString)))
                .andReturn();

        mockMvc.perform(get("/product/timestamp/{timestamp}", oldTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].name", is(oldName)))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void deleteNoId() throws Exception {
        String oldName = testProductName;
        String oldTimestampString = testTimestampString;
        String oldPriceString = testProduct.getPrice().toString();


        mockMvc.perform(delete("/product/"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", oldName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].timestamp", is(oldTimestampString)))
                .andReturn();

        mockMvc.perform(get("/product/timestamp/{timestamp}", oldTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].name", is(oldName)))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void deleteIdNotFound() throws Exception {
        String oldName = testProductName;
        String oldTimestampString = testTimestampString;
        String oldPriceString = testProduct.getPrice().toString();

        Long notFoundId = 10000L;


        mockMvc.perform(delete("/product/{id}", notFoundId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", oldName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].timestamp", is(oldTimestampString)))
                .andReturn();

        mockMvc.perform(get("/product/timestamp/{timestamp}", oldTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].name", is(oldName)))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void deleteIdNotAPositiveNumber() throws Exception {
        String oldName = testProductName;
        String oldTimestampString = testTimestampString;
        String oldPriceString = testProduct.getPrice().toString();

        Long notPositiveId = 0L;


        mockMvc.perform(delete("/product/{id}", notPositiveId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[?(@.field == 'delete.arg0')].message", hasItem(messages.get("error.product.id.notPositive"))))
                .andReturn();

        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", oldName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].timestamp", is(oldTimestampString)))
                .andReturn();

        mockMvc.perform(get("/product/timestamp/{timestamp}", oldTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].name", is(oldName)))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void deleteIdNotALongNumber() throws Exception {
        String oldName = testProductName;
        String oldTimestampString = testTimestampString;
        String oldPriceString = testProduct.getPrice().toString();

        String something = "notLongId";


        mockMvc.perform(delete("/product/{id}", something))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[?(@.field == null)].message", hasItem(messages.get("error.productEndpoint.notLong.message"))))
                .andReturn();

        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", oldName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].timestamp", is(oldTimestampString)))
                .andReturn();

        mockMvc.perform(get("/product/timestamp/{timestamp}", oldTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(oldPriceString)))
                .andExpect(jsonPath("$[0].name", is(oldName)))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void deletePositive() throws Exception {
        String oldName = testProductName;
        String oldTimestampString = testTimestampString;


        mockMvc.perform(delete("/product/{id}", testProduct.getId()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        entityManager.clear();

        mockMvc.perform(get("/product/name/{name}", oldName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        mockMvc.perform(get("/product/timestamp/{timestamp}", oldTimestampString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();
    }


    private void deleteNegativeNotProperId() {

    }

    private void saveNumerousProducts() {
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
        productService.saveProduct(cinnamonm5);
        productService.saveProduct(colam5);
    }


    private Product createProduct(String name, BigDecimal price, Instant timestamp) {
        return new Product(name, timestamp, price);
    }

    private long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
