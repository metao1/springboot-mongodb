package com.metao.webflux.mongo.springbootmongodb.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<OrderDoc, String> {

    @Tailable
    Flux<OrderDoc> findByProductId(String productId);

    @Tailable
    Flux<OrderDoc> findBy();
}
