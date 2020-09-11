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
package io.agilehandy.actuator.rsocket.client;

import io.agilehandy.actuator.rsocket.endpoint.DiscoveredRSocketEndpoint;
import io.agilehandy.actuator.rsocket.endpoint.DiscoveredRSocketOperation;
import io.agilehandy.actuator.rsocket.endpoint.ExposableRSocketEndpoint;
import io.agilehandy.actuator.rsocket.endpoint.RSocketEndpointsSupplier;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.MessageCondition;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Haytham Mohamed
 **/
public class RSocketEndpointMessageHandler extends RSocketMessageHandler {

	private final RSocketEndpointsSupplier supplier;

	public RSocketEndpointMessageHandler(RSocketEndpointsSupplier supplier) {
		this.supplier = supplier;
		this.setHandlerPredicate(null);
	}

	@PostConstruct
	public void setHandlers() {
		Collection<ExposableRSocketEndpoint> endpoints = supplier.getEndpoints();
		for (ExposableRSocketEndpoint endpoint: endpoints) {
			DiscoveredRSocketEndpoint discoveredRSocketEndpoint = (DiscoveredRSocketEndpoint) endpoint;
			List<DiscoveredRSocketOperation> operations =
					discoveredRSocketEndpoint.getOperations().stream()
							.map(op -> ((DiscoveredRSocketOperation) op))
							.collect(Collectors.toList());
			detectEndpointHandlerMethods(discoveredRSocketEndpoint.getEndpointBean(), operations);
		}

		this.setHandlerPredicate(null); // disable auto-detection

		printMappings();
	}

	protected final void detectEndpointHandlerMethods(Object bean, List<DiscoveredRSocketOperation> operations) {
		Map<Method, CompositeMessageCondition> methods = new HashMap<>();
		operations.stream()
				.forEach(op -> methods.put(op.getOperationMethod().getMethod(), this.getMappingForEndPointMethod(op)));
		methods.forEach((key, value) -> registerHandlerMethod(bean, key, value));

	}

	protected CompositeMessageCondition getMappingForEndPointMethod(DiscoveredRSocketOperation operation) {
		String[] patterns = {operation.getId()};
		patterns = this.processDestinations(patterns);
		return new CompositeMessageCondition(new MessageCondition[]{RSocketFrameTypeMessageCondition.EMPTY_CONDITION, new DestinationPatternsMessageCondition(patterns, this.obtainRouteMatcher())});
	}

	@Override
	protected CompositeMessageCondition extendMapping(CompositeMessageCondition composite, HandlerMethod handler) {
		return composite;
	}

	// for testing
	public void printMappings() {
		getHandlerMethods().entrySet().stream()
				.forEach(set -> System.out.println(set.getKey().getMessageConditions() + " ==>  " + set.getValue().getMethod().getName()));
	}

}
