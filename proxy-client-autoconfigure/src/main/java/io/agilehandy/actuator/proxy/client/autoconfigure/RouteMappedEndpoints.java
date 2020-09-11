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
package io.agilehandy.actuator.proxy.client.autoconfigure;

import io.agilehandy.actuator.rsocket.endpoint.RouteMappedEndpoint;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.EndpointsSupplier;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Haytham Mohamed
 **/
public class RouteMappedEndpoints implements Iterable<RouteMappedEndpoint> {

	private final String baseRoute;

	private final Map<EndpointId, RouteMappedEndpoint> endpoints;

	public RouteMappedEndpoints(String baseRoute, EndpointsSupplier<?> supplier) {
		Assert.notNull(supplier, "Supplier must not be null");
		this.baseRoute = (baseRoute != null) ? baseRoute : "";
		this.endpoints = getEndpoints(Collections.singleton(supplier));
	}

	public RouteMappedEndpoints(String baseRoute, Collection<EndpointsSupplier<?>> suppliers) {
		Assert.notNull(suppliers, "Suppliers must not be null");
		this.baseRoute = (baseRoute != null) ? baseRoute : "";
		this.endpoints = getEndpoints(suppliers);
	}

	private Map<EndpointId, RouteMappedEndpoint> getEndpoints(Collection<EndpointsSupplier<?>> suppliers) {
		Map<EndpointId, RouteMappedEndpoint> endpoints = new LinkedHashMap<>();
		suppliers.forEach((supplier) -> supplier.getEndpoints().forEach((endpoint) -> {
			if (endpoint instanceof RouteMappedEndpoint) {
				endpoints.put(endpoint.getEndpointId(), (RouteMappedEndpoint) endpoint);
			}
		}));
		return Collections.unmodifiableMap(endpoints);
	}

	public String getBaseRoute() {
		return this.baseRoute;
	}

	public RouteMappedEndpoint getEndpoint(EndpointId endpointId) {
		return this.endpoints.get(endpointId);
	}

	public String getRootRoute(EndpointId endpointId) {
		RouteMappedEndpoint endpoint = getEndpoint(endpointId);
		return (endpoint != null) ? endpoint.getRootRoute() : null;
	}

	private String getRoute(RouteMappedEndpoint endpoint) {
		return (endpoint != null) ? this.baseRoute + "." + endpoint.getRootRoute() : null;
	}

	public String getRoute(EndpointId endpointId) {
		return getRoute(getEndpoint(endpointId));
	}

	public Stream<RouteMappedEndpoint> stream() {
		return this.endpoints.values().stream();
	}

	@Override
	public Iterator<RouteMappedEndpoint> iterator() {
		return this.endpoints.values().iterator();
	}

	public Collection<String> getAllRootRoutes() {
		return asList(stream().map(RouteMappedEndpoint::getRootRoute));
	}

	private <T> List<T> asList(Stream<T> stream) {
		return stream.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}
}
