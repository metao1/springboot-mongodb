package com.metao.webflux.mongo.springbootmongodb;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.metao.webflux.mongo.springbootmongodb.application.MongoConfig;
import com.metao.webflux.mongo.springbootmongodb.application.TransactionConfig;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderDoc;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderRepository;
import com.metao.webflux.mongo.springbootmongodb.domain.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@Import({ TransactionConfig.class, OrderService.class, MongoConfig.class })
public class TailableOrderQueryTest extends BaseTestContainer {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void beforeEach() {
        var recreateCollection = mongoTemplate.dropCollection(OrderDoc.class)
                .then(Mono.defer(() -> {
                    var capped = CollectionOptions
                            .empty()
                            .size(1024 * 1024)
                            .maxDocuments(100)
                            .capped();
                    return mongoTemplate.createCollection(OrderDoc.class, capped);
                }));

        StepVerifier.create(recreateCollection)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void whenMultipleOrdersSave_thenUseTailableQuery_isOK() {
        var orders = new ConcurrentLinkedQueue<>();
        StepVerifier
                .create(write().then(write()))
                .expectNextCount(1)
                .verifyComplete();

        orderRepository.findByProductId("1")
                .doOnNext(orders::add)
                .doOnComplete(() -> {
                    System.out.println("complete");
                })
                .doOnTerminate(() -> {
                    System.out.println("terminate");
                })
                .subscribe();

        assertThat(orders).hasSize(2);

        StepVerifier
                .create(write().then(write()))
                .expectNextCount(1)
                .verifyComplete();

        assertThat(orders).hasSize(4);
    }

    private Mono<OrderDoc> write() {
        return orderRepository.save(new OrderDoc(UUID.randomUUID().toString(), "1"));
    }

}
