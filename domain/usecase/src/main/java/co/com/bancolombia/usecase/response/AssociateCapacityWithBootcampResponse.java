package co.com.bancolombia.usecase.response;

import java.util.List;

public class AssociateCapacityWithBootcampResponse extends CapacityResponse {
  private final Long bootcampId;

  public AssociateCapacityWithBootcampResponse(Long capacityId, String name, String description, List<TechnologyResponse> technologies, Long bootcampId) {
    super(capacityId, name, description, technologies);
    this.bootcampId = bootcampId;
  }

  public Long getBootcampId() {
    return bootcampId;
  }
}
