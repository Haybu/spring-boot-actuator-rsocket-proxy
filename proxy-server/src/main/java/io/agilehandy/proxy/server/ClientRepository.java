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

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Haytham Mohamed
 **/
@Repository
public class ClientRepository {

	Map<String, List<RSocketRequester>> connectedClients = new HashMap<>();

	public List<RSocketRequester> connectedClientsByServiceName(String serviceName) {
		List<RSocketRequester> connections = null;
		if (connectedClients.containsKey(serviceName)) {
			connections = connectedClients.get(serviceName);
		} else {
			connections = new ArrayList<>();
			connectedClients.put(serviceName, connections);
		}
		return connections;
	}

	public void add(String serviceName, RSocketRequester requester) {
		connectedClientsByServiceName(serviceName).add(requester);
	}

	public void remove(String serviceName, RSocketRequester requester) {
		connectedClientsByServiceName(serviceName).remove(requester);
	}

	public List<RSocketRequester> getAll() {
		List<RSocketRequester> all = new ArrayList<>();
		for(Map.Entry<String, List<RSocketRequester>> entry : connectedClients.entrySet()) {
			all.addAll(entry.getValue());
		}
		return all;
	}
}
