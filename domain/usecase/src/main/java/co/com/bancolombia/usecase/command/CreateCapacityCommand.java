package co.com.bancolombia.usecase.command;

import java.util.List;

public class CreateCapacityCommand {
  private final String name;
  private final String description;
  private final List<String> technologyNames;

  public CreateCapacityCommand(String name, String description, List<String> technologyNames) {
    this.name = name;
    this.description = description;
    this.technologyNames = technologyNames;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getTechnologyNames() {
    return technologyNames;
  }
}
