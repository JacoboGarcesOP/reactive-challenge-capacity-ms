package co.com.bancolombia.model.capacity.gateway;

import co.com.bancolombia.model.capacity.CapacityTechnology;
import co.com.bancolombia.model.capacity.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TechnologyGateway {
  Mono<Technology> associateTechnology(CapacityTechnology capacityTechnology);
  Flux<Technology> findByCapacityId(Long capacityId);
  Flux<Technology> findAll();
}
