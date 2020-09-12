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

import io.agilehandy.actuator.rsocket.client.RSocketEndpointMessageHandler;
import io.agilehandy.actuator.rsocket.endpoint.ExposableRSocketEndpoint;
import io.agilehandy.actuator.rsocket.endpoint.RSocketEndpointDiscoverer;
import io.agilehandy.actuator.rsocket.endpoint.RSocketEndpointsSupplier;
import io.agilehandy.actuator.rsocket.endpoint.RouteMapper;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.expose.IncludeExcludeEndpointFilter;
import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvokerAdvisor;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.boot.actuate.endpoint.invoke.convert.ConversionServiceParameterValueMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

import java.util.stream.Collectors;

/**
 * @author Haytham Mohamed
 **/

@Configuration(proxyBeanMethods = false)
//@ConditionalOnAvailableEndpoint
@ConditionalOnClass({ RSocketRequester.class, TcpServerTransport.class })
@AutoConfigureAfter({EndpointAutoConfiguration.class, RSocketStrategiesAutoConfiguration.class})
@EnableConfigurationProperties(RSocketActuatorProxyEndpointProperties.class)
public class RSocketActuatorProxyEndpointAutoConfiguration {

	private final ApplicationContext applicationContext;

	private final RSocketActuatorProxyEndpointProperties endpointProperties;

	public RSocketActuatorProxyEndpointAutoConfiguration(ApplicationContext applicationContext, RSocketActuatorProxyEndpointProperties endpointProperties) {
		this.applicationContext = applicationContext;
		this.endpointProperties = endpointProperties;
	}

	@Bean
	@ConditionalOnMissingBean(RouteMapper.class)
	public RouteMapper rsocketEndpointRouteMapper() {
		return new MappingRSocketEndpointRouteMapper(this.endpointProperties.getRouteMapping());
	}

	@Bean
	@ConditionalOnMissingBean(IncludeExcludeEndpointFilter.class)
	public IncludeExcludeEndpointFilter<ExposableRSocketEndpoint> rsocketExposeExcludePropertyEndpointFilter() {
		RSocketActuatorProxyEndpointProperties.Exposure exposure = this.endpointProperties.getExposure();
		return new IncludeExcludeEndpointFilter(ExposableRSocketEndpoint.class, exposure.getInclude(),
				exposure.getExclude(), IncludeExcludeEndpointFilter.DefaultIncludes.WEB);
	}

	@Bean
	@ConditionalOnMissingBean(ParameterValueMapper.class)
	public ConversionServiceParameterValueMapper conversionServiceParameterValueMapper() {
		return new ConversionServiceParameterValueMapper();
	}

	@Bean
	@ConditionalOnMissingBean(RSocketEndpointsSupplier.class)
	public RSocketEndpointDiscoverer rSocketEndpointDiscoverer(ParameterValueMapper parameterValueMapper,
	                                                           ObjectProvider<RouteMapper> endpointRouteMappers,
	                                                           ObjectProvider<OperationInvokerAdvisor> invokerAdvisors,
	                                                           ObjectProvider<EndpointFilter<ExposableRSocketEndpoint>> filters) {
		return new RSocketEndpointDiscoverer(this.applicationContext, endpointProperties.getBaseRoute(),
				parameterValueMapper,
				invokerAdvisors.orderedStream().collect(Collectors.toList()),
				filters.orderedStream().collect(Collectors.toList()),
				endpointRouteMappers.orderedStream().collect(Collectors.toList())
				);
	}

	@Bean
	public RSocketEndpointMessageHandler rSocketEndpointMessageHandler(RSocketEndpointDiscoverer discoverer
				, ObjectProvider<RSocketStrategies> rSocketStrategies
				, ObjectProvider<RSocketMessageHandlerCustomizer> customizers) {
		RSocketEndpointMessageHandler messageHandler = new RSocketEndpointMessageHandler(discoverer);
		messageHandler.setRSocketStrategies(rSocketStrategies.getIfAvailable());
		customizers.orderedStream().forEach((customizer) -> customizer.customize(messageHandler));
		//messageHandler.setHandlers();
		return messageHandler;
	}

}
