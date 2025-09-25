package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.usecase.response.CapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import co.com.bancolombia.usecase.response.GetCapacitiesResponse;
import co.com.bancolombia.usecase.response.FilterResponse;
import reactor.core.publisher.Mono;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetCapacityUseCase {
  private final CapacityGateway capacityGateway;
  private final TechnologyGateway technologyGateway;

  public GetCapacityUseCase(CapacityGateway capacityGateway, TechnologyGateway technologyGateway) {
    this.capacityGateway = capacityGateway;
    this.technologyGateway = technologyGateway;
  }

  public Mono<GetCapacitiesResponse> execute(int page, int size, String sortBy, String order) {
    if ("technologies".equalsIgnoreCase(sortBy)) {
      return capacityGateway.findAll()
        .concatMap(capacity -> technologyGateway
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
        )
        .collectList()
        .map(list -> {
          Comparator<CapacityResponse> comparator = Comparator.comparingInt(c -> c.getTechnologies().size());
          if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
          }
          List<CapacityResponse> sorted = list.stream().sorted(comparator).collect(Collectors.toList());
          int from = Math.max(page, 0) * Math.max(size, 0);
          int to = Math.min(sorted.size(), from + Math.max(size, 0));
          List<CapacityResponse> slice = from < to ? sorted.subList(from, to) : List.of();
          return new GetCapacitiesResponse(slice, new FilterResponse(page, size, sortBy, order));
        });
    }

    // Default: sort by name handled by the repository (DB level) with stable pagination
    return capacityGateway.findAllPagedSorted(page, size, sortBy, order)
      .concatMap(capacity -> technologyGateway
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
      )
      .collectList()
      .map(capacities -> new GetCapacitiesResponse(capacities, new FilterResponse(page, size, sortBy, order)));
  }
}
