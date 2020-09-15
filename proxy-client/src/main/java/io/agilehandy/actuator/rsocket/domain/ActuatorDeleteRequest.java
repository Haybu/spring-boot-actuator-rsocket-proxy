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

	private ActuatorDeleteRequest() {}

	public static class Builder {
		private ActuatorDeleteRequest requestToBuild;

		public Builder() {
			requestToBuild = new ActuatorDeleteRequest();
		}

		public ActuatorDeleteRequest build() {
			requestToBuild.setOperationType(OperationType.DELETE);
			ActuatorDeleteRequest requestBuilt = requestToBuild;
			requestToBuild = new ActuatorDeleteRequest();
			return requestBuilt;
		}

		public Builder withRoute(String route) {
			requestToBuild.setRoute(route);
			return this;
		}

		public Builder withClientName(String name) {
			requestToBuild.setClientName(name);
			return this;
		}

		public Builder withClientId(Integer id) {
			requestToBuild.setClientId(id);
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
