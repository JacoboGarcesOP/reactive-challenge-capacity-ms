package co.com.bancolombia.model.capacity;

import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;

import java.util.List;
import java.util.Objects;

public class Capacity {
  private final String TECHNOLOGIES_NULL_ERROR_MESSAGE = "The technologies list cannot be null.";
  private Id id;
  private Name name;
  private Description description;
  private List<Technology> technologies;

  public Capacity(Long id, String name, String description, List<Technology> technologies) {
    this.id = new Id(id);
    this.name = new Name(name);
    this.description = new Description(description);
    this.technologies = Objects.requireNonNull(technologies, TECHNOLOGIES_NULL_ERROR_MESSAGE);
  }

  public Capacity(Long id, String name, String description) {
    this.id = new Id(id);
    this.name = new Name(name);
    this.description = new Description(description);
  }

  public Capacity(String name, String description, List<Technology> technologies) {
    this.name = new Name(name);
    this.description = new Description(description);
    this.technologies = technologies;
  }

  public Capacity(String name, String description) {
    this.name = new Name(name);
    this.description = new Description(description);
  }

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public Description getDescription() {
    return description;
  }

  public void setDescription(Description description) {
    this.description = description;
  }

  public List<Technology> getTechnologies() {
    return technologies;
  }

  public void setTechnologies(List<Technology> technologies) {
    this.technologies = technologies;
  }
}
