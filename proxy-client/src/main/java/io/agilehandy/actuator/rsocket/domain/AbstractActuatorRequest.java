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
package io.agilehandy.actuator.rsocket.domain;

import org.springframework.boot.actuate.endpoint.OperationType;

import java.io.Serializable;
import java.util.List;

/**
 * @author Haytham Mohamed
 **/
public abstract class AbstractActuatorRequest implements Serializable {

	private String route;
	private String serviceName;
	private OperationType operationType;
	private List<Parameter> parameters;

	public boolean hasParameters() { return parameters == null || parameters.isEmpty() ; }

	public String getRoute() {
		return route;
	}

	protected void setRoute(String route) {
		this.route = route;
	}

	public String getServiceName() {
		return serviceName;
	}

	protected void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	protected void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	protected void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

}
