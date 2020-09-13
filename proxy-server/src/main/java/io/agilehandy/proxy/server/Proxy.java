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
package io.agilehandy.proxy.server;

import io.rsocket.util.DefaultPayload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * @author Haytham Mohamed
 **/

// may not be in use
//@RestController
public class Proxy {

	private final ClientRepository repository;

	public Proxy(ClientRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/actuator/connected/{route}")
	public Mono<String> getAllConnectedActuator2(@PathVariable String route) {
		return this.collectActuatorValues(Flux.fromStream(repository.getAll().stream()), route);

	}

	@GetMapping("/actuator/services/{name}/{route}")
	public Mono<String> getAllConnectedActuator(@PathVariable String name, @PathVariable String route) {
		return this.collectActuatorValues(Flux.fromStream(repository.connectedClientsByServiceName(name).stream()), route);
	}

	private Mono<String> collectActuatorValues(Flux<RSocketRequester> requesters, String route) {
		return requesters.flatMap(requester ->
				requester.route(route)
						.data(DefaultPayload.create(""))
						.retrieveMono(String.class))
				.collect(Collectors.joining("\n"));
	}
}
