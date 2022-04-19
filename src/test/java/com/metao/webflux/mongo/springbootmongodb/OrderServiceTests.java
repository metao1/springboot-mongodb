package com.metao.webflux.mongo.springbootmongodb;

import com.metao.webflux.mongo.springbootmongodb.application.TransactionConfig;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderDoc;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderRepository;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.test.StepVerifier;

@SpringBootTest
@Import({ TransactionConfig.class, OrderService.class })
public class OrderServiceTests extends BaseTestContainer {

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
