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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Haytham Mohamed
 **/

@ConfigurationProperties(prefix = "management.rsocket.proxy.endpoints")
public class RSocketActuatorProxyEndpointProperties {

	private final RSocketActuatorProxyEndpointProperties.Exposure exposure = new RSocketActuatorProxyEndpointProperties.Exposure();

	private String baseRoute = "actuator";

	/**
	 * Mapping between endpoint IDs and the route that should expose them.
	 */
	private final Map<String, String> routeMapping = new LinkedHashMap<>();

	public RSocketActuatorProxyEndpointProperties() {
	}

	public RSocketActuatorProxyEndpointProperties.Exposure getExposure() {
		return this.exposure;
	}

	public String getBaseRoute() {
		return this.baseRoute;
	}

	public void setBaseRoute(String route) {
		if (!StringUtils.isEmpty(route) && route.endsWith(".")) {
			route = route.substring(0, route.length() - 1);
		}
		this.baseRoute = route;
	}

	public Map<String, String> getRouteMapping() {
		return this.routeMapping;
	}

	public static class Exposure {
		private Set<String> include = new LinkedHashSet();
		private Set<String> exclude = new LinkedHashSet();

		public Exposure() {
		}

		public Set<String> getInclude() {
			return this.include;
		}

		public void setInclude(Set<String> include) {
			this.include = include;
		}

		public Set<String> getExclude() {
			return this.exclude;
		}

		public void setExclude(Set<String> exclude) {
			this.exclude = exclude;
		}
	}
}
