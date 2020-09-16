package io.agilehandy.proxy.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProxyServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyServerApplication.class, args);
	}

	/*@Bean
	public RSocketStrategies rsocketStrategies() {
		return  RSocketStrategies.builder()
				.encoders(encoders -> {
					encoders.add(new Jackson2CborEncoder());
					encoders.add(new Jackson2JsonEncoder());
				})
				.decoders(decoders -> {
						decoders.add(new Jackson2CborDecoder());
						decoders.add(new Jackson2JsonDecoder());
				})
				.build();
	}*/
}
