package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.CapacityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
  Mono<Boolean> existsByName(String name);
}
