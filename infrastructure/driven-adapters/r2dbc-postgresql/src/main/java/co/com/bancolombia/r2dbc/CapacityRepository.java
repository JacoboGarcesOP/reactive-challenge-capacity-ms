package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.CapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
  Mono<Boolean> existsByName(String name);

  @Query("SELECT capacity_id, name, description FROM capacity_schema.capacity ORDER BY name ASC LIMIT :limit OFFSET :offset")
  Flux<CapacityEntity> findAllOrderByNameAsc(int limit, int offset);
}
