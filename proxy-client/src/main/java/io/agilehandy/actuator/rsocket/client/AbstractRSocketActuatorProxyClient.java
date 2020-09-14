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

import io.agilehandy.actuator.rsocket.domain.ActuatorDeleteRequest;
import io.agilehandy.actuator.rsocket.domain.ActuatorReadRequest;
import io.agilehandy.actuator.rsocket.domain.ActuatorWriteRequest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

/**
 * @author Haytham Mohamed
 *
 * to interegate the proxy
 **/
public abstract class AbstractRSocketActuatorProxyClient {

	protected abstract RSocketRequester getProxyRSocketRequester();

	public Mono<String> read(final ActuatorReadRequest request) {
		return this.getProxyRSocketRequester()
				.route("actuator-read")
				.data(request)
				.retrieveMono(String.class)
				;
	}

	public Mono<Void> write(final ActuatorWriteRequest request) {
		return this.getProxyRSocketRequester()
				.route("actuator-write")
				.data(request)
				.send()
				;
	}

	public Mono<Void> delete(final ActuatorDeleteRequest request) {
		return this.getProxyRSocketRequester()
				.route("actuator-delete")
				.data(request)
				.send()
				;
	}

}
