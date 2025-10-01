package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import reactor.core.publisher.Flux;

public class GetAllCapacityIdsUseCase {
  private final CapacityGateway capacityGateway;

  public GetAllCapacityIdsUseCase(CapacityGateway capacityGateway) {
    this.capacityGateway = capacityGateway;
  }

  public Flux<Long> execute() {
    return capacityGateway.findAll().map(capacity -> capacity.getId().getValue());
  }
}
