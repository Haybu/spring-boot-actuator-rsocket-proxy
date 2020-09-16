/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agilehandy.sample;

import io.agilehandy.actuator.rsocket.client.ActuatorProxyClient;
import io.agilehandy.actuator.rsocket.domain.ActuatorReadRequest;
import io.agilehandy.actuator.rsocket.domain.ActuatorWriteRequest;
import io.agilehandy.actuator.rsocket.domain.Parameter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Haytham Mohamed
 **/

@RestController
public class SampleRestController {

	private final ActuatorProxyClient proxyClient;

	public SampleRestController(ActuatorProxyClient proxyClient) {
		this.proxyClient = proxyClient;
	}

	@GetMapping("/read/health")
	public Mono<String> readHealth() {
		// read connected actuator health
		return proxyClient.read(new ActuatorReadRequest.Builder()
					.withRoute("actuator.health.read").build())
				.doOnNext(System.out::println)
				.doOnError(err -> System.out.println(err.getCause()));
	}

	@GetMapping("/read/loggers")
	public Mono<String> readLoggers() {
		// read connected actuator loggers
		return proxyClient.read(new ActuatorReadRequest.Builder()
					.withRoute("actuator.loggers.read").build())
				.doOnNext(System.out::println)
				.doOnError(err -> System.out.println(err.getCause()));
	}

	@GetMapping("/write/loggers")
	public Mono<Void> writeOperation() {
		// write a loglevel to connected actuator loggers
		return proxyClient.write(new ActuatorWriteRequest.Builder()
					.withRoute("actuator.myloggers.write.map")
					.withParameter(new Parameter("name", "io.agilehandy.sample", String.class))
					.withParameter(new Parameter("configuredLevel", "TRACE", LogLevel.class))
					.build())
				.doOnNext(System.out::println)
				.doOnError(err -> System.out.println(err.getCause()));
	}

}
