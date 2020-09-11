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
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.util.stream.Collectors;

/**
 * @author Haytham Mohamed
 **/

@Configuration(proxyBeanMethods = false)
//@ConditionalOnAvailableEndpoint
@ConditionalOnClass({ RSocketRequester.class, io.rsocket.RSocket.class, TcpServerTransport.class })
@AutoConfigureAfter(EndpointAutoConfiguration.class)
@EnableConfigurationProperties(RSocketEndpointProperties.class)
public class RSocketEndpointAutoConfiguration {

	private final ApplicationContext applicationContext;

	private final RSocketEndpointProperties properties;

	public RSocketEndpointAutoConfiguration(ApplicationContext applicationContext, RSocketEndpointProperties properties) {
		this.applicationContext = applicationContext;
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean(RouteMapper.class)
	public RouteMapper rsocketEndpointRouteMapper() {
		return new MappingRSocketEndpointRouteMapper(this.properties.getPathMapping());
	}

	@Bean
	@ConditionalOnMissingBean(IncludeExcludeEndpointFilter.class)
	public IncludeExcludeEndpointFilter<ExposableWebEndpoint> rsocketExposeExcludePropertyEndpointFilter() {
		RSocketEndpointProperties.Exposure exposure = this.properties.getExposure();
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
		return new RSocketEndpointDiscoverer(this.applicationContext, parameterValueMapper,
				invokerAdvisors.orderedStream().collect(Collectors.toList()),
				filters.orderedStream().collect(Collectors.toList()),
				endpointRouteMappers.orderedStream().collect(Collectors.toList())
				);
	}
}
