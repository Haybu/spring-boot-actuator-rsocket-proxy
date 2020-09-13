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

import io.rsocket.transport.ClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.rsocket.RSocketRequester;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Haytham Mohamed
 **/
public class RSocketActuatorProxyClient extends AbstractRSocketActuatorProxyClient {

	Logger log = LoggerFactory.getLogger(RSocketActuatorProxyClient.class);

	private RSocketRequester proxyRSocketRequester;

	private final RSocketRequester.Builder builder;
	private final ClientTransport transport;

	public RSocketActuatorProxyClient(RSocketRequester.Builder builder, ClientTransport transport) {
		this.builder = builder;
		this.transport = transport;
	}

	@PostConstruct
	private void connect() {
		if (builder != null && transport != null) {
			builder.connect(transport)
					.doOnNext(requester -> {
						this.proxyRSocketRequester = requester;
						proxyRSocketRequester.rsocket()
								.onClose()
								.doOnError(error -> log.warn("Connection CLOSED"))
								.doFinally(consumer -> log.info("Client DISCONNECTED"))
								.subscribe();
					})
					.block();
		}
	}

	@PreDestroy
	private void close() {
		if (this.proxyRSocketRequester != null && this.proxyRSocketRequester.rsocket() != null) {
			this.proxyRSocketRequester.rsocket().dispose();
		}
	}

	@Override
	protected RSocketRequester getProxyRSocketRequester() {
		return proxyRSocketRequester;
	}
}
