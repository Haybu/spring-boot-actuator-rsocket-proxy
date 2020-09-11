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

import io.agilehandy.actuator.rsocket.client.RSocketActuatorProxyClient;
import io.agilehandy.actuator.rsocket.client.RSocketEndpointMessageHandler;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.netty.http.server.HttpServer;

/**
 * @author Haytham Mohamed
 **/

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ RSocketServer.class, RSocketStrategies.class, HttpServer.class, TcpServerTransport.class })
@ConditionalOnProperty(prefix = "management.rsocket.proxy", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(RSocketEndpointMessageHandler.class)
@AutoConfigureAfter({RSocketStrategiesAutoConfiguration.class, RSocketActuatorProxyEndpointAutoConfiguration.class})
@EnableConfigurationProperties(RSocketActuatorProxyClientProperties.class)
public class RSocketActuatorProxyClientAutoConfiguration {

	@ConditionalOnMissingBean
	//@Bean(destroyMethod = "close")
	RSocketActuatorProxyClient rSocketActuatorProxyClient(RSocketEndpointMessageHandler handler,
	                                                      RSocketActuatorProxyClientProperties properties) {
		return new RSocketActuatorProxyClient(handler, properties.createClientTransport());
	}

}
