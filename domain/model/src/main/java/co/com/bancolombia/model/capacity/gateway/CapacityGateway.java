package co.com.bancolombia.model.capacity.gateway;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityGateway {
  Mono<Boolean> existsByName(String name);
  Mono<Capacity> save(Capacity capacity);
  Flux<Capacity> findAll();
  Flux<Capacity> findAllPagedSorted(int page, int size, String sortBy, String order);
  Mono<Capacity> findById(Long capacityId);
  Flux<Capacity> findByBootcamp(Long bootcampId);
  Mono<CapacityBootcamp> associateCapacityBootcamp(CapacityBootcamp capacityBootcamp);
  Mono<CapacityBootcamp> findByBootcampIdAndCapacityId(Long bootcampId, Long capacityId);
  Mono<Long> countBootcampsByCapacityId(Long capacityId);
  Mono<Void> delete(Long capacityId);
  Mono<Void> deleteCapacityBootcampRelation(Long capacityId, Long bootcampId);
}
