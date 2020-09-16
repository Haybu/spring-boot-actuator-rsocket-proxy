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

import io.agilehandy.actuator.rsocket.domain.ActuatorDeleteRequest;
import io.agilehandy.actuator.rsocket.domain.ActuatorReadRequest;
import io.agilehandy.actuator.rsocket.domain.ActuatorWriteRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Haytham Mohamed
 **/

@RestController
public class ProxyRestController {

	private final ProxyService service;

	public ProxyRestController(ProxyService service) {
		this.service = service;
	}

	@GetMapping("/read/routes")
	public Mono<String> read() {
		// TODO: not here, but on the client side get RSocketEndpointMessageHandler to print out the mapped routes
		//ApplicationContext context = ... ;
		//RSocketEndpointMessageHandler handler = context.getBean(RSocketEndpointMessageHandler.class);
		return Mono.just("Operation is not supported. Please specify a certain route /read/routes/{route}");
	}

	@GetMapping("/read/routes/{route}")
	public Mono<String> read(String route) {
		return service.connectedActuatorRead(new ActuatorReadRequest.Builder()
				.withRoute(route)
				.build());
	}

	@GetMapping("/read/routes/{route}/clients/{clientName}")
	public Mono<String> read(String route, String clientName) {
		return service.connectedActuatorRead(new ActuatorReadRequest.Builder()
				.withRoute(route)
				.withClientName(clientName)
				.build());
	}

	@GetMapping("/read/routes/{route}/clients/{clientName}/ids/{clientId}")
	public Mono<String> read(String route, String clientName, Integer clientId) {
		return service.connectedActuatorRead(new ActuatorReadRequest.Builder()
				.withRoute(route)
				.withClientName(clientName)
				.withClientId(clientId)
				.build());
	}

	@PostMapping("/write")
	public Mono<Void> write(@RequestBody final ActuatorWriteRequest request) {
		return service.connectedActuatorUpdate(request);
	}

	@PostMapping("/delete")
	public Mono<Void> delete(@RequestBody final ActuatorDeleteRequest request) {
		return service.connectedActuatorUpdate(request);
	}

}
