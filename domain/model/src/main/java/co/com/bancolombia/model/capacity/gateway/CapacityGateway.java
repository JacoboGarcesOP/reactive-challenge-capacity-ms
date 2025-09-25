package co.com.bancolombia.model.capacity.gateway;

import co.com.bancolombia.model.capacity.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityGateway {
  Mono<Boolean> existsByName(String name);
  Mono<Capacity> save(Capacity capacity);
  Flux<Capacity> findAll();
}
