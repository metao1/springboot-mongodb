package com.metao.webflux.mongo.springbootmongodb.domain;

import java.time.Duration;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    @PostConstruct
    public void init() {
        Flux.interval(Duration.ofSeconds(1))
                .map(String::valueOf)
                .map(id -> new OrderDoc(UUID.randomUUID().toString(), id))
                .flatMap(orderRepository::save)
                .onErrorContinue(this::reportError)
                .subscribe(order -> System.out.println("Order created: " + order));
    }

    @ResponseBody
    @GetMapping(produces = "text/event-stream")    
    public Flux<OrderDoc> getOrders() {
        return orderRepository.findBy()
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void reportError(Throwable throwable1, Object object2) {
        System.out.println("Error: " + throwable1.getMessage());
    }
}
