package com.metao.webflux.mongo.springbootmongodb.application;

import com.metao.webflux.mongo.springbootmongodb.domain.OrderDoc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import reactor.core.publisher.Mono;

@Configuration
public class MongoConfig {

    /*
     * By default the mongo collection is not capped.
     * This snippet helps to convert the default mongo doc to capped collection.
     */
    @Bean
    public CommandLineRunner initData(ReactiveMongoOperations mongo) {
        return (String... args) -> {
            mongo.dropCollection(OrderDoc.class)
                    .then(Mono.defer(() -> {
                        var capped = CollectionOptions
                                .empty()
                                .size(1024 * 1024)
                                .maxDocuments(100)
                                .capped();
                        return mongo.createCollection(OrderDoc.class, capped);
                    }));
        };
    }
}
