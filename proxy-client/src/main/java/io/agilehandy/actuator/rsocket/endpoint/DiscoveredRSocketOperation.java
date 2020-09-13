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
import org.springframework.boot.actuate.endpoint.annotation.AbstractDiscoveredOperation;
import org.springframework.boot.actuate.endpoint.annotation.DiscoveredOperationMethod;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvoker;

import java.lang.reflect.Parameter;
import java.util.stream.Collectors;

/**
 * @author Haytham Mohamed
 **/
public class DiscoveredRSocketOperation extends AbstractDiscoveredOperation implements RSocketOperation {

	private final String id;

	public DiscoveredRSocketOperation(String baseRoute, String rootRoute, EndpointId endpointId, DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
		super(operationMethod, invoker);
		this.id = getId(baseRoute, rootRoute, endpointId, operationMethod);
	}

	// route in the default form of: actuator.baseRoute.endpointId.method name.[.selected param names]
	private String getId(String baseRoute, String rootRoute, EndpointId endpointId, DiscoveredOperationMethod operationMethod) {
		return baseRoute + "."
				//+ (!StringUtils.isEmpty(rootRoute)? rootRoute + "." : "")
				+ endpointId + "."
				+ operationMethod.getOperationType().toString().toLowerCase()
				+ operationMethod.getParameters().stream()
					.filter(parameter -> parameter.isMandatory())
					.map(parameter -> parameter.getName())
					.map(this::dotName)
					.collect(Collectors.joining())
				//+ Stream.of(operationMethod.getMethod().getParameters())
				//		.filter(this::hasSelector)
				//		.map(this::dotName)
				//		.collect(Collectors.joining())
			;
	}

	private boolean hasSelector(Parameter parameter) {
		return parameter.getAnnotation(Selector.class) != null;
	}

	private String dotName(Parameter parameter) {
		return "." + parameter.getName();
	}

	private String dotName(String parameter) {
		return "." + parameter;
	}

	@Override
	public String getId() {
		return id;
	}
}
