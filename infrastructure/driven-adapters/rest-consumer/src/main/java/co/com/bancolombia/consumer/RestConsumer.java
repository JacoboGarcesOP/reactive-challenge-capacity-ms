package co.com.bancolombia.consumer;

import co.com.bancolombia.model.capacity.CapacityTechnology;
import co.com.bancolombia.model.capacity.Technology;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.consumer.exception.BussinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestConsumer implements TechnologyGateway {
  private final WebClient client;

  @CircuitBreaker(name = "associateTechnology")
  @Override
  public Mono<Technology> associateTechnology(CapacityTechnology capacityTechnology) {
    ObjectRequest request = ObjectRequest.builder()
      .capacityId(capacityTechnology.getCapacityId().getValue())
      .technology(capacityTechnology.getTechnology().getValue())
      .build();

    return client
      .post()
      .uri("/associate")
      .body(Mono.just(request), ObjectRequest.class)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
      .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
      .bodyToMono(ObjectResponse.class)
      .map(resp -> new Technology(resp.getTechnologyId(), resp.getName(), resp.getDescription()));
  }

  @CircuitBreaker(name = "findByCapacityId")
  @Override
  public Flux<Technology> findByCapacityId(Long capacityId) {
    return client
      .get()
      .uri("/capacity/" + capacityId)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
      .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
      .bodyToFlux(ObjectResponse.class)
      .map(resp -> new Technology(resp.getTechnologyId(), resp.getName(), resp.getDescription()));
  }

  @CircuitBreaker(name = "findAll")
  @Override
  public Flux<Technology> findAll() {
    return client
      .get()
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
      .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
      .bodyToFlux(ObjectResponse.class)
      .map(resp -> new Technology(resp.getTechnologyId(), resp.getName(), resp.getDescription()));
  }

  @CircuitBreaker(name = "deleteTechnologiesByCapacity")
  @Override
  public Mono<List<Long>> deleteTechnologiesByCapacity(Long capacityId) {
    return client
      .delete()
      .uri("/capacity/" + capacityId)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
      .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
      .bodyToMono(Long[].class)
      .map(technologyIds -> List.of(technologyIds));
  }

  private Mono<? extends Throwable> map4xx(ClientResponse response) {
    return response.bodyToMono(ObjectResponse.class)
      .map(body -> new BussinessException(body.getDescription() != null ? body.getDescription() : "Client error"));
  }

  private Mono<? extends Throwable> map5xx(ClientResponse response) {
    return response.bodyToMono(String.class)
      .map(msg -> new RuntimeException("External service error: " + msg));
  }
}
