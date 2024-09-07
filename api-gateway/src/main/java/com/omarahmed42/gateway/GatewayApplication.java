package com.omarahmed42.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	@Bean
	RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("cart", r -> r.path("/cart/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://CART"))
				.route("catalog", r -> r.path("/catalog/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://CATALOG"))
				.route("checkout", r -> r.path("/checkout/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://CHECKOUT"))
				.route("inventory", r -> r.path("/inventory/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://INVENTORY"))
				.route("order", r -> r.path("/order/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://ORDER"))
				.route("payment", r -> r.path("/payment/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://PAYMENT"))
				.route("user", r -> r.path("/user/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://USER"))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
