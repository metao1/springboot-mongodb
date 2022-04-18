package com.metao.webflux.mongo.springbootmongodb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class OrderDoc {

    @Id
    private String id;

    private String productId;
    
}
