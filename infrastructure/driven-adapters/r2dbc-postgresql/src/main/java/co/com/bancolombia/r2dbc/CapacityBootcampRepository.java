package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.CapacityBootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CapacityBootcampRepository extends ReactiveCrudRepository<CapacityBootcampEntity, Long> {
  Mono<CapacityBootcampEntity> findByBootcampIdAndCapacityId(Long bootcampId, Long capacityId);
}
