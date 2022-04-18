package com.metao.webflux.mongo.springbootmongodb.domain;

import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final TransactionalOperator transactionalOperator;
    private final ReactiveMongoTemplate mongoTemplate;

    public Flux<OrderDoc> createOrder(String... productIds) {
        return transactionalOperator.execute(status -> buildOrder(mongoTemplate::insert, productIds));
    }

    private Flux<OrderDoc> buildOrder(Function<OrderDoc, Publisher<OrderDoc>> callback, String[] productIds) {
        return Flux
                .just(productIds)
                .filter(productId -> productId != null && !productId.isEmpty())
                .map(pid -> new OrderDoc(pid, pid))
                .flatMap(callback);
    }
}
