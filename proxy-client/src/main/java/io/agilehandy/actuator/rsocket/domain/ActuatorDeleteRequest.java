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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Haytham Mohamed
 **/
public class ActuatorDeleteRequest extends AbstractActuatorRequest {

	public static class Builder {
		private AbstractActuatorRequest requestToBuild;

		public Builder() {
			requestToBuild = new ActuatorDeleteRequest();
		}

		public AbstractActuatorRequest build() {
			requestToBuild.setOperationType(OperationType.DELETE);
			AbstractActuatorRequest requestBuilt = requestToBuild;
			requestToBuild = new ActuatorDeleteRequest();
			return requestBuilt;
		}

		public Builder withRoute(String route) {
			requestToBuild.setRoute(route);
			return this;
		}

		public Builder withServiceName(String name) {
			requestToBuild.setServiceName(name);
			return this;
		}

		public Builder withParameters(List<Parameter> parameters) {
			requestToBuild.setParameters(parameters);
			return this;
		}

		public Builder withParameter(Parameter parameter) {
			if (requestToBuild.getParameters() == null) {
				requestToBuild.setParameters(new ArrayList<>());
			}
			requestToBuild.getParameters().add(parameter);
			return this;
		}
	}
}
