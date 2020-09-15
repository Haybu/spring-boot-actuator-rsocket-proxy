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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agilehandy.actuator.rsocket.domain.AbstractActuatorRequest;
import io.agilehandy.actuator.rsocket.domain.Parameter;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Haytham Mohamed
 **/

@Service
public class ProxyService {

	Logger log = LoggerFactory.getLogger(ProxyController.class);

	private final ClientRepository clientRepository;
	private final ObjectMapper objectMapper;

	public ProxyService(ClientRepository clientRepository, ObjectMapper objectMapper) {
		this.clientRepository = clientRepository;
		this.objectMapper = objectMapper;
	}

	public boolean registerClientConnection(String clientName, Integer clientId, RSocketRequester requester) {
		boolean result = clientRepository.add(clientName, clientId, requester);
		log.info("connection of actuator client name {} and id {} is registered", clientName, clientId);
		return result;
	}

	public boolean unregisterClientConnection(String clientName, Integer clientId, RSocketRequester requester) {
		boolean result = clientRepository.remove(clientName, clientId, requester);
		log.info("connection of actuator client name {} and id {} is unregistered", clientName, clientId);
		return  result;
	}

	public Mono<String> connectedActuatorRead(final AbstractActuatorRequest request) {
		String route = request.getRoute();
		log.info("reading actuator from all connected clients with route {}", route);
		String data = this.getData(request);
		List<RSocketClientConnection> connections = this.targetConnections(request.getClientName());
		return Flux.fromIterable(connections)
				.flatMap(conn -> conn.getRSocketRequester().route(route)
								.data(DefaultPayload.create(data))
								.retrieveMono(Object.class)
								.map(this::objectToString)
								.map(str -> this.attachClientId(str, conn.getClientId()))
				)
				.collect(Collectors.joining(",\n"))
				.map(str -> "[\n " + str + " \n]")
				;
	}

	public Mono<Void> connectedActuatorUpdate(final AbstractActuatorRequest request) {
		String route = request.getRoute();
		log.info("updating actuator of all connected clients with route {}", route);
		String data = this.getData(request);
		List<RSocketClientConnection> connections = this.targetConnections(request.getClientName());
		return Flux.fromIterable(connections)
				.map(conn -> conn.getRSocketRequester())
				.flatMap(requester ->
						requester.route(route)
							.data(DefaultPayload.create(data))
							.send())
				.then();
	}

	private List<RSocketClientConnection> targetConnections(String clientName) {
		List<RSocketClientConnection> targets = null;
		if (StringUtils.isEmpty(clientName)) {
			targets = clientRepository.getAll();
			log.info("targeting actuator of All connected clients. (number of clients {}) "
					, targets.size());
		} else {
			targets = clientRepository.findAllByClientName(clientName);
			log.info("targeting actuator of connected clients with  name {} (number of clients {})"
					, clientName, targets.size());
		}
		return targets;
	}

	private String getData(AbstractActuatorRequest request) {
		String data = "{}";
		if (request.hasParameters()) {
			data = buildParametersString(request.getParameters());
		}
		log.info("actuator request data: " + data);
		return data;
	}

	private String buildParametersString(List<Parameter> parameters) {
		String json = parameters.stream()
				.map(this::buildParameterString)
				.collect(Collectors.joining(","));
		return "[ " + json + " ]";
	}

	private String attachClientId(String str, Integer clientId) {
		return "{ \"clientId\": " + clientId + ", \"actuator\": " + str + " }";
	}

	private String buildParameterString(Parameter parameter) {
		String name = parameter.getName();
		Class<?> type = parameter.getType();
		Object value = type.cast(parameter.getValue());
		String valueStr = "cannot-map-string-error";
		try {
			valueStr = objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "{ \"" + name + "\": " + valueStr + " }";
	}

	private String objectToString(Object obj) {
		Assert.notNull(obj, "Object should not be null to map to String");
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "cannot map Object to String";
	}

}
