package io.agilehandy.sample;

import io.agilehandy.actuator.rsocket.client.ActuatorProxyClient;
import io.agilehandy.actuator.rsocket.domain.ActuatorReadRequest;
import io.agilehandy.actuator.rsocket.endpoint.DiscoveredRSocketOperation;
import io.agilehandy.actuator.rsocket.endpoint.ExposableRSocketEndpoint;
import io.agilehandy.actuator.rsocket.endpoint.RSocketEndpointsSupplier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class SampleProxyClientApplication {

	private final RSocketEndpointsSupplier supplier;

	private final ActuatorProxyClient proxyClient;

	public SampleProxyClientApplication(RSocketEndpointsSupplier supplier, ActuatorProxyClient proxyClient) {
		this.supplier = supplier;
		this.proxyClient = proxyClient;
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleProxyClientApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner() {
		return args -> {
			//supplier.getEndpoints().stream()
					//.forEach(this::printEndPoint);

			// use the proxy client to read connected actuator health
			ActuatorReadRequest readRequest = new ActuatorReadRequest.Builder()
					.withRoute("actuator.features.read")
					.build();

			proxyClient.read(readRequest)
					.doOnNext(System.out::println)
					.doOnError(err -> System.out.println(err.getCause()))
					.subscribe();

		};
	}

	private void printEndPoint(ExposableRSocketEndpoint endpoint) {
		String id = endpoint.getEndpointId().toString();
		String rootRoute = endpoint.getRootRoute();
		List<String> methods = endpoint.getOperations().stream()
				.map(op -> ((DiscoveredRSocketOperation) op))
				.map(op -> "method: " + op.getOperationMethod().getMethod().getName()
						+ "   route: " + op.getId())
				.collect(Collectors.toList());
		System.out.println("id: " + id + " , rootPath: " + rootRoute );
		methods.stream().forEach(System.out::println);

	}

}
