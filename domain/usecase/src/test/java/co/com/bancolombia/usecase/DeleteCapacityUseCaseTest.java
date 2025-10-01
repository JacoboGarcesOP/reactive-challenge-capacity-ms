package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.usecase.exception.BussinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCapacityUseCaseTest {

  @Mock
  private CapacityGateway gateway;

  @Mock
  private TechnologyGateway technologyGateway;

  private DeleteCapacityUseCase deleteCapacityUseCase;

  @BeforeEach
  void setUp() {
    deleteCapacityUseCase = new DeleteCapacityUseCase(gateway, technologyGateway);
  }

  @Test
  @DisplayName("Should delete all capacities when bootcampId is provided and capacities exist")
  void shouldDeleteAllCapacitiesWhenBootcampIdIsProvidedAndCapacitiesExist() {
    // Arrange
    Long bootcampId = 1L;
    Capacity capacity1 = new Capacity(1L, "Capacity 1", "Description 1");
    Capacity capacity2 = new Capacity(2L, "Capacity 2", "Description 2");
    List<Capacity> capacities = Arrays.asList(capacity1, capacity2);

    when(gateway.findByBootcamp(bootcampId)).thenReturn(Flux.fromIterable(capacities));
    when(gateway.countBootcampsByCapacityId(1L)).thenReturn(Mono.just(1L));
    when(gateway.countBootcampsByCapacityId(2L)).thenReturn(Mono.just(1L));
    when(technologyGateway.deleteTechnologiesByCapacity(1L)).thenReturn(Mono.just(Arrays.asList(10L, 11L)));
    when(technologyGateway.deleteTechnologiesByCapacity(2L)).thenReturn(Mono.just(Arrays.asList(12L, 13L)));
    when(gateway.delete(1L)).thenReturn(Mono.empty());
    when(gateway.delete(2L)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(deleteCapacityUseCase.execute(bootcampId))
      .expectNext(Arrays.asList(1L, 2L))
      .verifyComplete();

    verify(gateway).findByBootcamp(bootcampId);
    verify(gateway).countBootcampsByCapacityId(1L);
    verify(gateway).countBootcampsByCapacityId(2L);
    verify(technologyGateway).deleteTechnologiesByCapacity(1L);
    verify(technologyGateway).deleteTechnologiesByCapacity(2L);
    verify(gateway).delete(1L);
    verify(gateway).delete(2L);
  }

  @Test
  @DisplayName("Should delete only relation when capacity is associated to multiple bootcamps")
  void shouldDeleteOnlyRelationWhenCapacityIsAssociatedToMultipleBootcamps() {
    // Arrange
    Long bootcampId = 1L;
    Capacity capacity1 = new Capacity(1L, "Capacity 1", "Description 1");
    Capacity capacity2 = new Capacity(2L, "Capacity 2", "Description 2");
    List<Capacity> capacities = Arrays.asList(capacity1, capacity2);

    when(gateway.findByBootcamp(bootcampId)).thenReturn(Flux.fromIterable(capacities));
    when(gateway.countBootcampsByCapacityId(1L)).thenReturn(Mono.just(2L));
    when(gateway.countBootcampsByCapacityId(2L)).thenReturn(Mono.just(1L));
    when(gateway.deleteCapacityBootcampRelation(1L, bootcampId)).thenReturn(Mono.empty());
    when(technologyGateway.deleteTechnologiesByCapacity(2L)).thenReturn(Mono.just(Arrays.asList(12L, 13L)));
    when(gateway.delete(2L)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(deleteCapacityUseCase.execute(bootcampId))
      .expectNext(Arrays.asList(1L, 2L))
      .verifyComplete();

    verify(gateway).findByBootcamp(bootcampId);
    verify(gateway).countBootcampsByCapacityId(1L);
    verify(gateway).countBootcampsByCapacityId(2L);
    verify(gateway).deleteCapacityBootcampRelation(1L, bootcampId);
    verify(technologyGateway).deleteTechnologiesByCapacity(2L);
    verify(gateway).delete(2L);
    verify(gateway, never()).delete(1L);
    verify(technologyGateway, never()).deleteTechnologiesByCapacity(1L);
  }

  @Test
  @DisplayName("Should return error when bootcampId is null")
  void shouldReturnErrorWhenBootcampIdIsNull() {
    // Act & Assert
    StepVerifier.create(deleteCapacityUseCase.execute(null))
      .expectError(BussinessException.class)
      .verify();

    verify(gateway, never()).findByBootcamp(any());
  }

  @Test
  @DisplayName("Should return error when bootcamp has no capacities")
  void shouldReturnErrorWhenBootcampHasNoCapacities() {
    // Arrange
    Long bootcampId = 1L;
    when(gateway.findByBootcamp(bootcampId)).thenReturn(Flux.empty());

    // Act & Assert
    StepVerifier.create(deleteCapacityUseCase.execute(bootcampId))
      .expectError(BussinessException.class)
      .verify();

    verify(gateway).findByBootcamp(bootcampId);
    verify(gateway, never()).countBootcampsByCapacityId(any());
  }

  @Test
  @DisplayName("Should handle mixed scenarios - some capacities deleted completely, some relations only")
  void shouldHandleMixedScenarios() {
    // Arrange
    Long bootcampId = 1L;
    Capacity capacity1 = new Capacity(1L, "Capacity 1", "Description 1");
    Capacity capacity2 = new Capacity(2L, "Capacity 2", "Description 2");
    Capacity capacity3 = new Capacity(3L, "Capacity 3", "Description 3");
    List<Capacity> capacities = Arrays.asList(capacity1, capacity2, capacity3);

    when(gateway.findByBootcamp(bootcampId)).thenReturn(Flux.fromIterable(capacities));
    when(gateway.countBootcampsByCapacityId(1L)).thenReturn(Mono.just(1L));
    when(gateway.countBootcampsByCapacityId(2L)).thenReturn(Mono.just(3L));
    when(gateway.countBootcampsByCapacityId(3L)).thenReturn(Mono.just(2L));
    when(technologyGateway.deleteTechnologiesByCapacity(1L)).thenReturn(Mono.just(Arrays.asList(10L)));
    when(gateway.delete(1L)).thenReturn(Mono.empty());
    when(gateway.deleteCapacityBootcampRelation(2L, bootcampId)).thenReturn(Mono.empty());
    when(gateway.deleteCapacityBootcampRelation(3L, bootcampId)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(deleteCapacityUseCase.execute(bootcampId))
      .expectNext(Arrays.asList(1L, 2L, 3L))
      .verifyComplete();

    verify(gateway).findByBootcamp(bootcampId);
    verify(gateway).countBootcampsByCapacityId(1L);
    verify(gateway).countBootcampsByCapacityId(2L);
    verify(gateway).countBootcampsByCapacityId(3L);
    verify(technologyGateway).deleteTechnologiesByCapacity(1L);
    verify(gateway).delete(1L);
    verify(gateway).deleteCapacityBootcampRelation(2L, bootcampId);
    verify(gateway).deleteCapacityBootcampRelation(3L, bootcampId);
    verify(gateway, never()).delete(2L);
    verify(gateway, never()).delete(3L);
    verify(technologyGateway, never()).deleteTechnologiesByCapacity(2L);
    verify(technologyGateway, never()).deleteTechnologiesByCapacity(3L);
  }

  @Test
  @DisplayName("Should propagate error when gateway throws exception")
  void shouldPropagateErrorWhenGatewayThrowsException() {
    // Arrange
    Long bootcampId = 1L;
    RuntimeException gatewayError = new RuntimeException("Gateway error");
    when(gateway.findByBootcamp(bootcampId)).thenReturn(Flux.error(gatewayError));

    // Act & Assert
    StepVerifier.create(deleteCapacityUseCase.execute(bootcampId))
      .expectError(RuntimeException.class)
      .verify();

    verify(gateway).findByBootcamp(bootcampId);
  }
}
