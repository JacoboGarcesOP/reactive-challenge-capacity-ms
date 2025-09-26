package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.response.CapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import reactor.core.publisher.Flux;

public class GetCapacityByBootcampUseCase {
  private final CapacityGateway capacityGateway;
  private final TechnologyGateway technologyGateway;

  public GetCapacityByBootcampUseCase(CapacityGateway capacityGateway, TechnologyGateway technologyGateway) {
    this.capacityGateway = capacityGateway;
    this.technologyGateway = technologyGateway;
  }

  public Flux<CapacityResponse> execute(Long bootcmapId) {
    return capacityGateway.findByBootcamp(bootcmapId)
      .flatMap(capacity -> technologyGateway
        .findByCapacityId(capacity.getId().getValue())
        .map(technology -> new TechnologyResponse(
            technology.getId().getValue(),
            technology.getName().getValue(),
            technology.getDescription().getValue()
          )
        ).collectList()
        .map(technologies -> new CapacityResponse(
            capacity.getId().getValue(),
            capacity.getName().getValue(),
            capacity.getDescription().getValue(),
            technologies
          )
        )
      , 20);
  }
}
