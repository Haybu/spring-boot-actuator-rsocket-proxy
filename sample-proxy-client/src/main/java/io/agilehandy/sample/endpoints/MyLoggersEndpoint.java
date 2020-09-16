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
package io.agilehandy.sample.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Haytham Mohamed
 **/

@Component
@Endpoint(id="myloggers")
public class MyLoggersEndpoint {

	private Log log = LogFactory.getLog(MyLoggersEndpoint.class);

	private final LoggersEndpoint delegate;
	private final ObjectMapper mapper;

	public MyLoggersEndpoint(LoggersEndpoint delegate, ObjectMapper mapper) {
		this.delegate = delegate;
		this.mapper = mapper;
	}

	@WriteOperation
	public void configureFeature(@Selector Map<String, Object> map) {
		String data = (String)map.get("dataUtf8");
		Map<String, String> mapData = null;
		try {
			mapData = mapper.readValue(data, Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String name = mapData.get("name");
		String configuredLevel = mapData.get("configuredLevel");
		delegate.configureLogLevel(name, LogLevel.valueOf(configuredLevel));
	}
}
