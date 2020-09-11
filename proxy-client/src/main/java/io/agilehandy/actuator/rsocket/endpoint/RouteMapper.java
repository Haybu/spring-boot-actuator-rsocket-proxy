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

import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;

@FunctionalInterface
public interface RouteMapper {
	String getRootRoute(EndpointId endpointId);

	static String getRootRoute(List<RouteMapper> routeMappers, EndpointId endpointId) {
		Assert.notNull(endpointId, "EndpointId must not be null");
		if (routeMappers != null) {
			Iterator itr = routeMappers.iterator();

			while(itr.hasNext()) {
				RouteMapper mapper = (RouteMapper)itr.next();
				String path = mapper.getRootRoute(endpointId);
				if (StringUtils.hasText(path)) {
					return path;
				}
			}
		}

		return endpointId.toString();
	}
}
