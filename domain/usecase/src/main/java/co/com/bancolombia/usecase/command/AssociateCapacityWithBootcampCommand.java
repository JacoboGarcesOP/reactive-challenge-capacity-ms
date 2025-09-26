package co.com.bancolombia.usecase.command;

public class AssociateCapacityWithBootcampCommand {
  private final Long capacityId;
  private final Long bootcampId;

  public AssociateCapacityWithBootcampCommand(Long capacityId, Long bootcampId) {
    this.capacityId = capacityId;
    this.bootcampId = bootcampId;
  }

  public Long getCapacityId() {
    return capacityId;
  }

  public Long getBootcampId() {
    return bootcampId;
  }
}
