package com.metao.webflux.mongo.springbootmongodb;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public class BaseTestContainer {

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

}
