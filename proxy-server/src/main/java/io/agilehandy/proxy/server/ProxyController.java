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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author Haytham Mohamed
 **/

@Controller
public class ProxyController {

	Logger log = LoggerFactory.getLogger(ProxyController.class);

	private final ProxyService service;

	public ProxyController(ProxyService service) {
		this.service = service;
	}

	@ConnectMapping("client-connect")
	public void clientConnect(RSocketRequester clientRequester, @Payload Map<String, Object> setupData) {
		clientRequester.rsocket()
				.onClose()
				.doFirst(() -> {
					// Add new client to a clients list
					if (service.registerClientConnection((String)setupData.get("client-name")
							, (Integer)setupData.get("client-id"), clientRequester)) {
						log.info("Client with name {} and id {} is connected to proxy."
								, setupData.get("client-name"), setupData.get("client-id"));
					} else {
						log.info("Client with name {} and id {} is not connected to proxy."
								, setupData.get("client-name"), setupData.get("client-id"));
					}
				})
				.doOnError(error -> {
					// Warn when channels are closed by clients
					log.warn("Channel to client with name {} and id {} is closed."
							, setupData.get("client-name"), setupData.get("client-id"));
				})
				.doFinally(consumer -> {
					// Remove disconnected clients from the client list
					if (service.unregisterClientConnection((String)setupData.get("client-name")
							, (Integer)setupData.get("client-id"), clientRequester)) {
						log.info("Client with name {} and id {} is disconnected from proxy."
								, setupData.get("client-name"), setupData.get("client-id"));
					}
				})
				.subscribe();
	}

	@MessageMapping("actuator-read")
	public Mono<String> read(final ActuatorReadRequest request) {
		return service.connectedActuatorRead(request);
	}

	@MessageMapping("actuator-write")
	public Mono<Void> write(final ActuatorWriteRequest request) {
		return service.connectedActuatorUpdate(request);
	}

	@MessageMapping("actuator-delete")
	public Mono<Void> delete(final ActuatorDeleteRequest request) {
		return service.connectedActuatorUpdate(request);
	}

}
