package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.CapacityBootcamp;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.command.AssociateCapacityWithBootcampCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.AssociateCapacityWithBootcampResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import reactor.core.publisher.Mono;

public class AssociateCapacityWithBootcampUseCase {
  private final String CAPACITY_NOT_FOUND_MESSAGE = "The capacity has not been found.";
  private final String ASSOCIATION_ALREADY_EXISTS_MESSAGE = "The capacity is already associated with this bootcamp.";
  private final CapacityGateway capacityGateway;
  private final TechnologyGateway technologyGateway;


  public AssociateCapacityWithBootcampUseCase(CapacityGateway capacityGateway, TechnologyGateway technologyGateway) {
    this.capacityGateway = capacityGateway;
    this.technologyGateway = technologyGateway;
  }

  public Mono<AssociateCapacityWithBootcampResponse> execute(AssociateCapacityWithBootcampCommand command) {
    return capacityGateway.findById(command.getCapacityId())
      .switchIfEmpty(Mono.error(new BussinessException(CAPACITY_NOT_FOUND_MESSAGE)))
      .flatMap(capacity -> {
        Long capacityId = capacity.getId().getValue();
        Long bootcampId = command.getBootcampId();

        return capacityGateway.findByBootcampIdAndCapacityId(bootcampId, capacityId)
          .flatMap(existingAssociation ->
            Mono.error(new BussinessException(ASSOCIATION_ALREADY_EXISTS_MESSAGE))
          )
          .cast(AssociateCapacityWithBootcampResponse.class)
          .switchIfEmpty(
            capacityGateway.associateCapacityBootcamp(new CapacityBootcamp(bootcampId, capacityId))
              .flatMap(capacityBootcamp -> technologyGateway
                .findByCapacityId(capacityId)
                .collectList()
                .map( techs -> new AssociateCapacityWithBootcampResponse(
                  capacity.getId().getValue(),
                  capacity.getName().getValue(),
                  capacity.getDescription().getValue(),
                  techs.stream().map(t -> new TechnologyResponse(
                    t.getId().getValue(),
                    t.getName().getValue(),
                    t.getDescription().getValue())).toList(),
                    capacityBootcamp.getBootcampId().getValue()
                  )
                )
              )
          );
      });
  }
}
