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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Haytham Mohamed
 **/
@Repository
public class ClientRepository {

	Logger log = LoggerFactory.getLogger(ClientRepository.class);

	Map<String, List<RSocketClientConnection>> connectedClients = new HashMap<>();

	public List<RSocketClientConnection> findAllByClientName(String clientName) {
		return (connectedClients.containsKey(clientName))?
			 connectedClients.get(clientName) : new ArrayList<RSocketClientConnection>();
	}

	public boolean add(String clientName, Integer clientId, RSocketRequester requester) {
		if (!connectedClients.containsKey(clientName)) {
			RSocketClientConnection connection = new RSocketClientConnection(clientId, requester);
			List<RSocketClientConnection> list = new ArrayList<>();
			list.add(connection);
			connectedClients.put(clientName, list);
		} else {
			List<RSocketClientConnection> connections = connectedClients.get(clientName);
			Optional<RSocketRequester> existing = connections.stream()
					.filter(c -> c.getClientId().equals(clientId) && c.getRSocketRequester().equals(requester))
					.map(c -> c.getRSocketRequester())
					.findFirst();
			if (existing != null && existing.isPresent()) {
				log.info("A proxy connection already exists for client named " + clientName + " with id " + clientId);
				return false;
			} else { // add the new connection
				connections.add(new RSocketClientConnection(clientId, requester));
			}
		}
		return true;
	}

	public boolean remove(String clientName, Integer clientId, RSocketRequester requester) {
		boolean result = true;
		if (!connectedClients.containsKey(clientName)) {
			return false;
		}
		List<RSocketClientConnection> connections = connectedClients.get(clientName);
		Optional<RSocketClientConnection> connection = connections.stream()
				.filter(c -> c.getClientId().equals(clientId) && requester.equals(requester))
				.findAny();

		if (connection != null && connection.isPresent()) {
			connections.remove(connection.get());
		} else {
			result = false;
		}

		if (connections.isEmpty()) {
			connectedClients.remove(clientName);
		}
		return result;
	}

	public List<RSocketClientConnection> getAll() {
		List<RSocketClientConnection> all = new ArrayList<>();
		for(Map.Entry<String, List<RSocketClientConnection>> entry : connectedClients.entrySet()) {
			all.addAll(entry.getValue());
		}
		return all;
	}
}
