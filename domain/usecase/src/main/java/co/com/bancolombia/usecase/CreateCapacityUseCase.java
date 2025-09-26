package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnology;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.command.CreateCapacityCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.CapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

public class CreateCapacityUseCase {
  private final String TECHNOLOGIES_DUPLICATED_MESSAGE = "The capacity should not have duplicated technologies.";
  private final String CAPACITY_LOWER_BOUND_MESSAGE = "The capacity should have 3 technologies minimum.";
  private final String CAPACITY_UPPER_BOUND_MESSAGE = "The capacity should have 20 technologies maximum.";
  private final String TECHNOLOGY_NOT_FOUND_MESSAGE = "Some technologies have not been found.";
  private final String CAPACITY_DUPLICATED_MESSAGE = "The capacity name cannot be duplicated.";
  private final CapacityGateway capacityGateway;
  private final TechnologyGateway technologyGateway;

  public CreateCapacityUseCase(CapacityGateway capacityGateway, TechnologyGateway technologyGateway) {
    this.capacityGateway = capacityGateway;
    this.technologyGateway = technologyGateway;
  }

  public Mono<CapacityResponse> execute(CreateCapacityCommand command) {
    if (isWrongMinQuantityTechnologies(command.getTechnologyNames())) {
      return Mono.error(new BussinessException(CAPACITY_LOWER_BOUND_MESSAGE));
    }

    if (isWrongMaxQuantityTechnologies(command.getTechnologyNames())) {
      return Mono.error(new BussinessException(CAPACITY_UPPER_BOUND_MESSAGE));
    }

    if (existsDuplicatedTechnologies(command.getTechnologyNames())) {
      return Mono.error(new BussinessException(TECHNOLOGIES_DUPLICATED_MESSAGE));
    }

    return capacityGateway.existsByName(command.getName())
      .flatMap(exists -> {
        if (Boolean.TRUE.equals(exists)) {
          return Mono.error(new BussinessException(CAPACITY_DUPLICATED_MESSAGE));
        }

        return validateTechnologiesExisting(command.getTechnologyNames())
          .flatMap(valid -> capacityGateway.save(new Capacity(command.getName(), command.getDescription()))
          .flatMap(capacity -> Flux
            .fromIterable(command.getTechnologyNames())
            .flatMap(t -> technologyGateway.associateTechnology(new CapacityTechnology(t, capacity.getId().getValue())), 20)
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
          ));
      });
  }

  private Boolean isWrongMinQuantityTechnologies(List<String> technologyNames) {
    return technologyNames.size() < 3;
  }

  private Boolean isWrongMaxQuantityTechnologies(List<String> technologyNames) {
    return technologyNames.size() > 20;
  }

  private Boolean existsDuplicatedTechnologies(List<String> technologyNames) {
    return technologyNames.stream().distinct().count() != technologyNames.size();
  }

  private Mono<Boolean> validateTechnologiesExisting(List<String> technologyNames) {
    return technologyGateway.findAll()
      .map(t -> t.getName().getValue())
      .collectList()
      .map(existingNames -> new HashSet<>(existingNames).containsAll(technologyNames))
      .flatMap(allExist -> allExist ? Mono.just(true) : Mono.error(new BussinessException(TECHNOLOGY_NOT_FOUND_MESSAGE)));
  }
}
