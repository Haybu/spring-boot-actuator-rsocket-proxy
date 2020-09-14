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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Haytham Mohamed
 **/
@Component
@Endpoint(id = "features")
public class FeaturesEndpoint {

	Logger log = LoggerFactory.getLogger(FeaturesEndpoint.class);

	private Map<String, Feature> features = new ConcurrentHashMap<>();

	public FeaturesEndpoint() {
		features.put("All", new Feature(true));
	}

	@ReadOperation
	public Mono<Map<String, Feature>> features() {
		log.info("features endpoint read operation method got called.");
		return Mono.just(features);
	}

	@ReadOperation
	public Mono<Feature> feature(@Selector String name) {
		log.info("features endpoint method read operation got called.");
		return Mono.just(features.get(name));
	}

	@WriteOperation
	public Mono<Void> configureFeature(@Selector String name, Feature feature) {
		System.out.println("features endpoint write operation method got called.");
		return Mono.create(t -> features.put(name, feature)).then();
	}

	@DeleteOperation
	public Mono<Void> deleteFeature(@Selector String name) {
		System.out.println("features endpoint delete operation method got called.");
		return Mono.create(t -> features.remove(name)).then();
	}

	public static class Feature {
		private Boolean enabled;
		public Feature(Boolean b) { this.enabled=b;}
		public Boolean getEnabled() {
			return enabled;
		}
		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}
		@Override
		public String toString() { return "Feature is: " + enabled; }
	}

}
