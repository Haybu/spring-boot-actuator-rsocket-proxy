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
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Haytham Mohamed
 **/
public class DiscoveredRSocketOperation extends AbstractDiscoveredOperation implements RSocketOperation {

	private final String id;

	public DiscoveredRSocketOperation(String rootRoute, EndpointId endpointId, DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
		super(operationMethod, invoker);
		Method method = operationMethod.getMethod();
		this.id = getId(rootRoute, endpointId, method);
	}

	private String getId(String rootRoute, EndpointId endpointId, Method method) {
		return (!StringUtils.isEmpty(rootRoute)? rootRoute + "." : "")
				+ endpointId
				+ Stream.of(method.getParameters())
						.filter(this::hasSelector)
						.map(this::dotName)
						.collect(Collectors.joining());
	}

	private boolean hasSelector(Parameter parameter) {
		return parameter.getAnnotation(Selector.class) != null;
	}

	private String dotName(Parameter parameter) {
		return "." + parameter.getName();
	}

	@Override
	public String getId() {
		return id;
	}
}
