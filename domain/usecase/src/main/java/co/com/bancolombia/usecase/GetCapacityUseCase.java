package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.response.CapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import reactor.core.publisher.Flux;

public class GetCapacityUseCase {
  private final CapacityGateway capacityGateway;
  private final TechnologyGateway technologyGateway;

  public GetCapacityUseCase(CapacityGateway capacityGateway, TechnologyGateway technologyGateway) {
    this.capacityGateway = capacityGateway;
    this.technologyGateway = technologyGateway;
  }

  public Flux<CapacityResponse> execute() {
    return capacityGateway.findAll()
      .flatMap(capacity -> technologyGateway
        .findByCapacityId(capacity.getId().getValue())
        .collectList()
        .map(technologies -> new CapacityResponse(
            capacity.getId().getValue(),
            capacity.getName().getValue(),
            capacity.getDescription().getValue(),
            technologies
              .stream()
              .map(technology -> new TechnologyResponse(
                technology.getId().getValue(),
                technology.getName().getValue(),
                technology.getDescription().getValue())
              ).toList()
          )
        )
      );
  }
}
