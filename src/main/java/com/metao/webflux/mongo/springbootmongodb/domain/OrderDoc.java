package com.metao.webflux.mongo.springbootmongodb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class OrderDoc {

    @Id
    private String id;

    private String productId;

    @Override
    public String toString() {
        return "OrderDoc{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}
