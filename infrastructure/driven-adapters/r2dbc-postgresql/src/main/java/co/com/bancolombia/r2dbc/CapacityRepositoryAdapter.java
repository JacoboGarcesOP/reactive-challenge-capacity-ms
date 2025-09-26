package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityBootcamp;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.r2dbc.entity.CapacityBootcampEntity;
import co.com.bancolombia.r2dbc.entity.CapacityEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CapacityRepositoryAdapter implements CapacityGateway {
  private final CapacityRepository capacityRepository;
  private final CapacityBootcampRepository capacityBootcampRepository;

  public CapacityRepositoryAdapter(CapacityRepository capacityRepository, CapacityBootcampRepository capacityBootcampRepository) {
    this.capacityRepository = capacityRepository;
    this.capacityBootcampRepository = capacityBootcampRepository;
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

  @Override
  public Flux<Capacity> findAllPagedSorted(int page, int size, String sortBy, String order) {
    int limit = Math.max(size, 0);
    int offset = Math.max(page, 0) * limit;

    if ("desc".equalsIgnoreCase(order)) {
      return capacityRepository
        .findAllOrderByNameAsc(limit, offset)
        .collectList()
        .flatMapMany(list -> {
          java.util.Collections.reverse(list);
          return reactor.core.publisher.Flux.fromIterable(list);
        })
        .map(entity -> new Capacity(entity.getId(), entity.getName(), entity.getDescription()));
    }
    return capacityRepository
      .findAllOrderByNameAsc(limit, offset)
      .map(entity -> new Capacity(entity.getId(), entity.getName(), entity.getDescription()));
  }

  @Override
  public Mono<Capacity> findById(Long capacityId) {
    return capacityRepository
      .findById(capacityId)
      .map(entity -> new Capacity(entity.getId(), entity.getName(), entity.getDescription()));
  }

  @Override
  public Flux<Capacity> findByBootcamp(Long bootcampId) {
    return capacityRepository
      .findByBootcamp(bootcampId)
      .map(entity -> new Capacity(entity.getId(), entity.getName(), entity.getDescription()));
  }

  @Override
  public Mono<CapacityBootcamp> associateCapacityBootcamp(CapacityBootcamp capacityBootcamp) {
    Long bootId = capacityBootcamp.getBootcampId().getValue();
    Long capId = capacityBootcamp.getCapacityId().getValue();

    CapacityBootcampEntity entity = CapacityBootcampEntity.builder()
      .bootcampId(bootId)
      .capacityId(capId)
      .build();

    return capacityBootcampRepository.save(entity)
      .map(savedEntity -> new CapacityBootcamp(
        savedEntity.getBootcampId(),
        savedEntity.getCapacityId()
      ));
  }

  @Override
  public Mono<CapacityBootcamp> findByBootcampIdAndCapacityId(Long bootcampId, Long capacityId) {
    return capacityBootcampRepository.findByBootcampIdAndCapacityId(bootcampId, capacityId)
      .map(entity -> new CapacityBootcamp(entity.getBootcampId(), entity.getCapacityId()));
  }
}
