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
package io.agilehandy.actuator.rsocket.endpoint;

import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.annotation.DiscoveredOperationMethod;
import org.springframework.boot.actuate.endpoint.annotation.EndpointDiscoverer;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvoker;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvokerAdvisor;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;

/**
 * @author Haytham Mohamed
 **/
public class RSocketEndpointDiscoverer extends EndpointDiscoverer<ExposableRSocketEndpoint, RSocketOperation> implements RSocketEndpointsSupplier {

	// look at MappingWebEndpointPathMapper
	private final List<RouteMapper> endpointRouteMappers;
	private final String baseRoute;

	public RSocketEndpointDiscoverer(ApplicationContext applicationContext, String baseRoute
			, ParameterValueMapper parameterValueMapper
			, Collection<OperationInvokerAdvisor> invokerAdvisors
			, Collection<EndpointFilter<ExposableRSocketEndpoint>> endpointFilters
			, List<RouteMapper> endpointRouteMappers) {
		super(applicationContext, parameterValueMapper, invokerAdvisors, endpointFilters);
		this.endpointRouteMappers = endpointRouteMappers;
		this.baseRoute = baseRoute;
	}

	@Override
	protected ExposableRSocketEndpoint createEndpoint(Object endpointBean, EndpointId id, boolean enabledByDefault, Collection<RSocketOperation> operations) {
		String rootRoute = RouteMapper.getRootRoute(this.endpointRouteMappers, id);
		return new DiscoveredRSocketEndpoint(this, endpointBean, id, rootRoute, enabledByDefault, operations);
	}

	@Override
	protected RSocketOperation createOperation(EndpointId id, DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
		String rootRoute = RouteMapper.getRootRoute(this.endpointRouteMappers, id);
		return new DiscoveredRSocketOperation(baseRoute, rootRoute, id, operationMethod, invoker);
	}

	@Override
	protected OperationKey createOperationKey(RSocketOperation operation) {
		return new OperationKey(operation.getId(),
				() -> "RSocket Operation route " + operation.getId());
	}
}
