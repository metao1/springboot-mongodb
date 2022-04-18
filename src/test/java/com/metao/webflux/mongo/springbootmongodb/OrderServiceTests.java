package com.metao.webflux.mongo.springbootmongodb;

import java.time.Duration;

import com.metao.webflux.mongo.springbootmongodb.application.TransactionConfiguration;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderDoc;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderRepository;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import reactor.test.StepVerifier;

@SpringBootTest
@Testcontainers
@Import({ TransactionConfiguration.class, OrderService.class })
public class OrderServiceTests {

        private static final MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
                        .withExposedPorts(27017)
                        .withReuse(true)
                        .waitingFor(Wait.forListeningPort())
                        .withStartupTimeout(Duration.ofSeconds(60))
                        .withNetworkAliases("mongo");

        @BeforeAll
        static void beforeAll() {
                mongo.start();
        }

        @AfterAll
        static void tearDown() {
                mongo.stop();
        }

        @DynamicPropertySource
        static void mongoProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.data.mongodb.uri", () -> mongo.getReplicaSetUrl());
        }

        @Autowired
        private OrderService orderService;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private ReactiveMongoTemplate mongoTemplate;

        @BeforeEach
        public void setUp() {
                var createIfMissed = mongoTemplate.collectionExists(OrderDoc.class)
                                .filter(x -> !x)
                                .flatMap(exist -> mongoTemplate.createCollection(OrderDoc.class))
                                .thenReturn(true);

                StepVerifier.create(createIfMissed)
                                .expectNext(true)
                                .verifyComplete();
        }

        @Test
        void testCreateOrder_withNull_thenRollback() {
                var orders = orderRepository.deleteAll()
                                .thenMany(orderService.createOrder("1", "2", "3", null))
                                .thenMany(orderRepository.findAll());

                StepVerifier.create(orders)
                                .expectNextCount(0)
                                .verifyError();

                StepVerifier.create(orderRepository.findAll())
                                .expectNextCount(0)
                                .verifyComplete();
        }

        @Test
        void testCreateManyOrders_isOk() {
                var orders = orderService.createOrder("1", "2", "3")
                                .thenMany(orderRepository.findAll());

                StepVerifier.create(orders)
                                .expectNextCount(3)
                                .verifyComplete();

        }
}
