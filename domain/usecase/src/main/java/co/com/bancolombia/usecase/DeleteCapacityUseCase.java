package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class DeleteCapacityUseCase {
  private final String BOOTCAMP_ID_CANNOT_BE_NULL_MESSAGE = "Bootcamp ID cannot be null";
  private final String BOOTCAMP_NOT_FOUND_MESSAGE = "Bootcamp has not been found. Bootcamp id: ";
  private final CapacityGateway gateway;
  private final TechnologyGateway technologyGateway;

  public DeleteCapacityUseCase(CapacityGateway gateway, TechnologyGateway technologyGateway) {
    this.gateway = gateway;
    this.technologyGateway = technologyGateway;
  }

  public Mono<List<Long>> execute(Long bootcampId) {
    if (bootcampId == null) {
      return Mono.error(new BussinessException(BOOTCAMP_ID_CANNOT_BE_NULL_MESSAGE));
    }

    return gateway.findByBootcamp(bootcampId)
      .collectList()
      .flatMap(capacities -> {
        if (capacities.isEmpty()) {
          return Mono.error(new BussinessException(BOOTCAMP_NOT_FOUND_MESSAGE + bootcampId));
        }

        return Flux.fromIterable(capacities)
          .flatMap(capacity -> {
            Long capacityId = capacity.getId().getValue();
            
            return gateway.countBootcampsByCapacityId(capacityId)
              .flatMap(bootcampCount -> {
                if (bootcampCount == 1) {
                  return technologyGateway.deleteTechnologiesByCapacity(capacityId)
                    .then(gateway.delete(capacityId))
                    .then(Mono.just(capacityId));
                } else {
                  return gateway.deleteCapacityBootcampRelation(capacityId, bootcampId)
                    .then(Mono.just(capacityId));
                }
              });
          })
          .collectList();
      });
  }
}
