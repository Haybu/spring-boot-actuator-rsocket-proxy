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
package io.agilehandy.actuator.proxy.client.autoconfigure;


import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

/**
 * @author Haytham Mohamed
 **/

@ConfigurationProperties(prefix = "management.rsocket.proxy")
public class RSocketActuatorProxyClientProperties {

	private String host = "localhost";

	private int port = 7002;

	private Transport transport = Transport.TCP;

	private boolean secure = false;

	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	ClientTransport createClientTransport() {
		final TcpClient tcpClient = TcpClient.create().host(this.host).port(this.port);
		return this.transport.create(this.secure ? tcpClient.secure() : tcpClient);
	}

	enum Transport {

		/**
		 * TCP transport protocol.
		 */
		TCP {
			@Override
			ClientTransport create(TcpClient tcpClient) {
				return TcpClientTransport.create(tcpClient);
			}
		},

		/**
		 * WebSocket transport protocol.
		 */
		WEBSOCKET {
			@Override
			ClientTransport create(TcpClient tcpClient) {
				return WebsocketClientTransport.create(HttpClient.from(tcpClient), "/");
			}
		};

		abstract ClientTransport create(TcpClient tcpClient);
	}

}
