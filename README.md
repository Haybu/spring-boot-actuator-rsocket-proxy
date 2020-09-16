## Spring Boot RSocket Actuator Proxy

##### To obtain actuator management endpoints from ensemble of connected clients via a proxy

![spring boot actuator proxy](images/spring-boot-actuator-proxy.png)

The idea is to reach out and collect actuator management endpoints from 
all instances of a particular microservice (or probably instances of ALL services).

### Actuator Proxy Client

provided you are using spring boot and having the `spring-boot-starter-rsocket` and `spring-boot-starter-actuator` dependencies included, 
add the `actuator-proxy-client-spring-boot-starter` dependency:

```xml
<dependency>
    <groupId>io.agilehandy</groupId>
    <artifactId>actuator-proxy-client-spring-boot-starter</artifactId>
    <version>{version}</version>
</dependency>
```

Set also the following properties in your `application.yaml` or `application.properties` file to connect to the proxy,
to specify a service name, and to expose proxied endpoints. You can define the exposed list of proxied endpoints to be 
the same as the ones that are included via the service's configured actuator.

```yaml
management:
  rsocket:
    client:
      service-name: ${spring.application.name}
      client-id: 1
      endpoints:
        exposure:
          include: ${management.endpoints.web.exposure.include}
      proxy:
        host: localhost
        port: 7002
```

Another client with a typical setup (with different `client-id` if has same `service-name`) 
can obtain a collection of the health from all connected clients or from those of a specific 
service. To do that you can inject and use the `ActuatorProxyClient` bean as follows:

```java
@Component
class MyClass {
    private final ActuatorProxyClient proxyClient;

    public MyClass(ActuatorProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    public void readHealth() {
    // create a read operation request object
    ActuatorReadRequest readRequest = new ActuatorReadRequest.Builder()
        .withRoute("actuator.health.read")  // route formed from: baseRoot.endpointId.operation (where baseRoot has a value of actuator)
        .build();

    // use the proxy client to read connected actuator health
    proxyClient.read(readRequest)
        .doOnNext(System.out::println)
        .subscribe(); 
    }
   
}
```

Beside the reading request domain object `ActuatorReadRequest`, there are also `ActuatorWriteRequest` and `ActuatorDeleteRequest` domain objects 
by which you can perform proxied write and delete operations respectively on connected clients. All these request domain objects
can include operation parameters as needed.

The output result from each obtained proxied actuator endpoint includes the ID of each client. For example, the
output of the snippet above (with breviy) would look like:

```json
[
   {
      "clientId":1,
      "actuator":{
         "status":"UP"
      }
   },
   {
      "clientId":2,
      "actuator":{
         "status":"UP"
      }
   }
]
```

The client would automatically map routes to exposed actuator endpoints. You can log out and view all mapped routes to actuator 
endpoints by setting `logging.level.io.agilehandy.actuator.rsocket.client=DEBUG` in your properties file.

An endpoint route takes a form of: `baseRoot.endpointId.operation.[parameter-name *]`
where the baseRoot defaults to `actuator` value.

For example: 
*   to read a client health the route would look like `actuator.health.read`
*   to read a client health by path name parameter, the route would look like: `actuator.health.read.name`. The parameter name
and value would be passed via the request object.

A sub-module sample `sample-proxy-client` is provided to see a client in action.

### Actuator Proxy

Use the published docker image `haybu/spring-actuator-rsocket-proxy:v0 ` to run the RSocket Actuator Proxy. 
A shell script `./scripts/spring-actuator-rsocket-proxy.sh` is provided to run the proxy. 
The proxy would run on port 7002.

### Work In-Progress
*   Write and delete operations. 
*   Parameterized operations.



