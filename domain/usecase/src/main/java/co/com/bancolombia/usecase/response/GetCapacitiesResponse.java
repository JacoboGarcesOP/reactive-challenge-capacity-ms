package co.com.bancolombia.usecase.response;

import java.util.List;

public class GetCapacitiesResponse {
  private final List<CapacityResponse> capacities;
  private final FilterResponse filter;

  public GetCapacitiesResponse(List<CapacityResponse> capacities, FilterResponse filter) {
    this.capacities = capacities;
    this.filter = filter;
  }

  public List<CapacityResponse> getCapacities() { return capacities; }
  public FilterResponse getFilter() { return filter; }
}


