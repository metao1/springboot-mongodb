package com.metao.webflux.mongo.springbootmongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringBootMongodbApplication {

	static {
		//BlockHound.install();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMongodbApplication.class, args);
	}

}
