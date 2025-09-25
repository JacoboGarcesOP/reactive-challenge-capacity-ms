package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.r2dbc.entity.CapacityEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CapacityRepositoryAdapter implements CapacityGateway {
  private final CapacityRepository capacityRepository;

  public CapacityRepositoryAdapter(CapacityRepository capacityRepository) {
    this.capacityRepository = capacityRepository;
  }

  @Override
  public Mono<Boolean> existsByName(String name) {
    return capacityRepository.existsByName(name);
  }

  @Override
  public Mono<Capacity> save(Capacity capacity) {
    CapacityEntity entity = new CapacityEntity(null, capacity.getName().getValue(), capacity.getDescription().getValue());
    return capacityRepository.save(entity)
      .map(saved -> {
        capacity.setId(new co.com.bancolombia.model.capacity.values.Id(saved.getId()));
        return capacity;
      });
  }

  @Override
  public Flux<Capacity> findAll() {
    return capacityRepository.findAll().map(entity -> new Capacity(entity.getId(), entity.getName(), entity.getDescription()));
  }
}
