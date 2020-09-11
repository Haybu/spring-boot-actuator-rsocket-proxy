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
package io.agilehandy.actuator.rsocket.client;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.ClientTransport;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Haytham Mohamed
 **/
public class RSocketActuatorProxyClient {

	private volatile RSocket connection;
	private volatile boolean requestedDisconnect = false;

	private final RSocketEndpointMessageHandler handler;
	private final ClientTransport transport;

	public RSocketActuatorProxyClient(RSocketEndpointMessageHandler handler, ClientTransport transport) {
		this.handler = handler;
		this.transport = transport;
	}

	@PostConstruct
	public void connect() {
		RSocketConnector.create()
				.acceptor(handler.responder())
				.connect(transport)
				.doOnNext(connection -> this.connection = connection)
				.flatMap(socket -> socket.onClose()
						.map(v -> 1) // https://github.com/rsocket/rsocket-java/issues/819
						.onErrorReturn(1))
				.repeat(() -> !requestedDisconnect)
				.subscribe();
	}

	@PreDestroy
	public void close() {
		this.requestedDisconnect = true;
		if (this.connection != null) {
			this.connection.dispose();
		}
	}

}
