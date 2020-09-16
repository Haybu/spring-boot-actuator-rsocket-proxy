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
package io.agilehandy.proxy.server;

/**
 * @author Haytham Mohamed
 **/
public class ActuatorResult {

	String clientName;
	Integer clientId;
	Object actuator;

	public ActuatorResult(String clientName, Integer clientId, Object actuator) {
		this.clientName = clientName;
		this.clientId = clientId;
		this.actuator = actuator;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Object getActuator() {
		return actuator;
	}

	public void setActuator(Object actuator) {
		this.actuator = actuator;
	}

}
