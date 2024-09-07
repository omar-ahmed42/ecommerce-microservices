package com.omarahmed42.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stripe.Stripe;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import jakarta.annotation.PostConstruct;
import net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration;

@SpringBootApplication(exclude = { GrpcDiscoveryClientAutoConfiguration.class })
@Deployment(resources = "classpath:payment-purchase.bpmn")
public class PaymentApplication {

	@Value("${stripe.api.key}")
	private String stripeSecretApiKey;

	@PostConstruct
	public void setup() {
		Stripe.apiKey = stripeSecretApiKey;
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
