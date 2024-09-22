package com.omarahmed42.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration;

@SpringBootApplication(exclude = { GrpcDiscoveryClientAutoConfiguration.class })
@EnableJpaAuditing(modifyOnCreate = false)
@Deployment(resources = "classpath:place-order.bpmn")
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

}
