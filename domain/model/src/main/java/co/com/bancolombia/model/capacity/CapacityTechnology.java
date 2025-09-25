package co.com.bancolombia.model.capacity;

import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;

public class CapacityTechnology {
  private Name technology;
  private Id capacityId;

  public CapacityTechnology(String technology, Long capacityId) {
    this.technology = new Name(technology);
    this.capacityId = new Id(capacityId);
  }

  public Name getTechnology() {
    return technology;
  }

  public void setTechnology(Name technology) {
    this.technology = technology;
  }

  public Id getCapacityId() {
    return capacityId;
  }

  public void setCapacityId(Id capacityId) {
    this.capacityId = capacityId;
  }
}
