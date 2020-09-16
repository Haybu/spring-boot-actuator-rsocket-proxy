package io.agilehandy.sample;

import io.agilehandy.actuator.rsocket.endpoint.DiscoveredRSocketOperation;
import io.agilehandy.actuator.rsocket.endpoint.ExposableRSocketEndpoint;
import io.agilehandy.actuator.rsocket.endpoint.RSocketEndpointsSupplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
//@EnableScheduling
public class SampleProxyClientApplication {

	private Log log = LogFactory.getLog(SampleProxyClientApplication.class);

	private final RSocketEndpointsSupplier supplier;

	public SampleProxyClientApplication(RSocketEndpointsSupplier supplier) {
		this.supplier = supplier;
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleProxyClientApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner() {
		return args -> {
			//supplier.getEndpoints().stream()
					//.forEach(this::printEndPoint);
		};
	}

	//@Scheduled(fixedDelay = 2000)
	private void printLogMessage() {
		log.trace("This is a TRACE level message");
		log.debug("This is a DEBUG level message");
		log.info("This is an INFO level message");
		log.warn("This is a WARN level message");
		log.error("This is an ERROR level message");
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
