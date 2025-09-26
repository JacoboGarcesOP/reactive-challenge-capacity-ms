package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityBootcamp;
import co.com.bancolombia.model.capacity.Technology;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.usecase.command.AssociateCapacityWithBootcampCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.AssociateCapacityWithBootcampResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssociateCapacityWithBootcampUseCase Tests")
class AssociateCapacityWithBootcampUseCaseTest {

  @Mock
  private CapacityGateway capacityGateway;

  @Mock
  private TechnologyGateway technologyGateway;

  private AssociateCapacityWithBootcampUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase = new AssociateCapacityWithBootcampUseCase(capacityGateway, technologyGateway);
  }

  @Test
  @DisplayName("Should associate capacity with bootcamp successfully")
  void shouldAssociateCapacityWithBootcampSuccessfully() {
    Long capacityId = 1L;
    Long bootcampId = 100L;
    AssociateCapacityWithBootcampCommand command = new AssociateCapacityWithBootcampCommand(capacityId, bootcampId);

    Capacity capacity = new Capacity("Payments Squad", "Handles all payment features");
    capacity.setId(new Id(capacityId));

    Technology java = new Technology(10L, "Java", "Java 21 LTS");
    Technology spring = new Technology(11L, "Spring Boot", "Spring Boot Framework");

    when(capacityGateway.findById(capacityId)).thenReturn(Mono.just(capacity));
    when(capacityGateway.findByBootcampIdAndCapacityId(bootcampId, capacityId)).thenReturn(Mono.empty());
    when(capacityGateway.associateCapacityBootcamp(any(CapacityBootcamp.class)))
      .thenReturn(Mono.just(new CapacityBootcamp(bootcampId, capacityId)));
    when(technologyGateway.findByCapacityId(capacityId)).thenReturn(Flux.just(java, spring));

    StepVerifier.create(useCase.execute(command))
      .assertNext(response -> {
        assert response instanceof AssociateCapacityWithBootcampResponse;
        AssociateCapacityWithBootcampResponse r = (AssociateCapacityWithBootcampResponse) response;
        assert r.getCapacityId().equals(capacityId);
        assert r.getBootcampId().equals(bootcampId);
        assert r.getTechnologies().stream().map(TechnologyResponse::getName).toList().contains("Java");
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("Should error when capacity does not exist")
  void shouldErrorWhenCapacityDoesNotExist() {
    Long capacityId = 999L;
    Long bootcampId = 100L;
    AssociateCapacityWithBootcampCommand command = new AssociateCapacityWithBootcampCommand(capacityId, bootcampId);

    when(capacityGateway.findById(capacityId)).thenReturn(Mono.empty());

    StepVerifier.create(useCase.execute(command))
      .expectErrorMatches(t -> t instanceof BussinessException && t.getMessage().equals("The capacity has not been found."))
      .verify();
  }
}


