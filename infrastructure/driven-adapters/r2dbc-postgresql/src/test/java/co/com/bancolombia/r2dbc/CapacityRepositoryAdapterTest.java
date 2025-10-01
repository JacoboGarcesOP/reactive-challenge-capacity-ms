package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityBootcamp;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.r2dbc.entity.CapacityBootcampEntity;
import co.com.bancolombia.r2dbc.entity.CapacityEntity;
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
class CapacityRepositoryAdapterTest {

  @Mock
  private CapacityRepository capacityRepository;

  @Mock
  private CapacityBootcampRepository capacityBootcampRepository;

  private CapacityRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new CapacityRepositoryAdapter(capacityRepository, capacityBootcampRepository);
  }

  @Test
  @DisplayName("Should count bootcamps by capacity id successfully")
  void shouldCountBootcampsByCapacityIdSuccessfully() {
    // Arrange
    Long capacityId = 1L;
    Long expectedCount = 3L;
    when(capacityBootcampRepository.countByCapacityId(capacityId)).thenReturn(Mono.just(expectedCount));

    // Act & Assert
    StepVerifier.create(adapter.countBootcampsByCapacityId(capacityId))
      .expectNext(expectedCount)
      .verifyComplete();

    verify(capacityBootcampRepository).countByCapacityId(capacityId);
  }

  @Test
  @DisplayName("Should delete capacity successfully")
  void shouldDeleteCapacitySuccessfully() {
    // Arrange
    Long capacityId = 1L;
    when(capacityRepository.deleteById(capacityId)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(adapter.delete(capacityId))
      .verifyComplete();

    verify(capacityRepository).deleteById(capacityId);
  }

  @Test
  @DisplayName("Should delete capacity bootcamp relation successfully")
  void shouldDeleteCapacityBootcampRelationSuccessfully() {
    // Arrange
    Long capacityId = 1L;
    Long bootcampId = 2L;
    when(capacityBootcampRepository.deleteByCapacityIdAndBootcampId(capacityId, bootcampId))
      .thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(adapter.deleteCapacityBootcampRelation(capacityId, bootcampId))
      .verifyComplete();

    verify(capacityBootcampRepository).deleteByCapacityIdAndBootcampId(capacityId, bootcampId);
  }

  @Test
  @DisplayName("Should propagate error when count bootcamps fails")
  void shouldPropagateErrorWhenCountBootcampsFails() {
    // Arrange
    Long capacityId = 1L;
    RuntimeException error = new RuntimeException("Database error");
    when(capacityBootcampRepository.countByCapacityId(capacityId)).thenReturn(Mono.error(error));

    // Act & Assert
    StepVerifier.create(adapter.countBootcampsByCapacityId(capacityId))
      .expectError(RuntimeException.class)
      .verify();

    verify(capacityBootcampRepository).countByCapacityId(capacityId);
  }

  @Test
  @DisplayName("Should propagate error when delete capacity fails")
  void shouldPropagateErrorWhenDeleteCapacityFails() {
    // Arrange
    Long capacityId = 1L;
    RuntimeException error = new RuntimeException("Database error");
    when(capacityRepository.deleteById(capacityId)).thenReturn(Mono.error(error));

    // Act & Assert
    StepVerifier.create(adapter.delete(capacityId))
      .expectError(RuntimeException.class)
      .verify();

    verify(capacityRepository).deleteById(capacityId);
  }

  @Test
  @DisplayName("Should propagate error when delete relation fails")
  void shouldPropagateErrorWhenDeleteRelationFails() {
    // Arrange
    Long capacityId = 1L;
    Long bootcampId = 2L;
    RuntimeException error = new RuntimeException("Database error");
    when(capacityBootcampRepository.deleteByCapacityIdAndBootcampId(capacityId, bootcampId))
      .thenReturn(Mono.error(error));

    // Act & Assert
    StepVerifier.create(adapter.deleteCapacityBootcampRelation(capacityId, bootcampId))
      .expectError(RuntimeException.class)
      .verify();

    verify(capacityBootcampRepository).deleteByCapacityIdAndBootcampId(capacityId, bootcampId);
  }
}