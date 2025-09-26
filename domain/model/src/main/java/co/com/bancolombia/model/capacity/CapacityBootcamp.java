package co.com.bancolombia.model.capacity;

import co.com.bancolombia.model.capacity.values.Id;

public class CapacityBootcamp {
  private Id bootcampId;
  private Id capacityId;

  public CapacityBootcamp(Long bootcampId, Long capacityId) {
    this.bootcampId = new Id(bootcampId);
    this.capacityId = new Id(capacityId);
  }

  public Id getBootcampId() {
    return bootcampId;
  }

  public void setBootcampId(Id bootcampId) {
    this.bootcampId = bootcampId;
  }

  public Id getCapacityId() {
    return capacityId;
  }

  public void setCapacityId(Id capacityId) {
    this.capacityId = capacityId;
  }
}
