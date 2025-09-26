package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityBootcamp;
import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import co.com.bancolombia.r2dbc.entity.CapacityBootcampEntity;
import co.com.bancolombia.r2dbc.entity.CapacityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CapacityRepositoryAdapter Tests")
class CapacityRepositoryAdapterTest {

  @Mock
  private CapacityRepository capacityRepository;

  @Mock
  private CapacityBootcampRepository capacityBootcampRepository;

  @InjectMocks
  private CapacityRepositoryAdapter capacityRepositoryAdapter;

  private Capacity testCapacity;
  private CapacityEntity testCapacityEntity;

  @BeforeEach
  void setUp() {
    testCapacity = new Capacity("Test Capacity", "Test Description");
    testCapacityEntity = new CapacityEntity(1L, "Test Capacity", "Test Description");
  }

  @Test
  @DisplayName("Should map associateCapacityBootcamp correctly")
  void shouldMapAssociateCapacityBootcampCorrectly() {
    // Given
    CapacityBootcampEntity saved = new CapacityBootcampEntity(null, 100L, 1L);
    when(capacityBootcampRepository.save(any(CapacityBootcampEntity.class))).thenReturn(Mono.just(saved));

    // When
    Mono<CapacityBootcamp> result = capacityRepositoryAdapter.associateCapacityBootcamp(new CapacityBootcamp(100L, 1L));

    // Then
    StepVerifier.create(result)
      .assertNext(cb -> {
        org.assertj.core.api.Assertions.assertThat(cb.getBootcampId().getValue()).isEqualTo(100L);
        org.assertj.core.api.Assertions.assertThat(cb.getCapacityId().getValue()).isEqualTo(1L);
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("Should find association by bootcamp and capacity")
  void shouldFindAssociationByBootcampAndCapacity() {
    // Given
    CapacityBootcampEntity entity = new CapacityBootcampEntity(null, 200L, 2L);
    when(capacityBootcampRepository.findByBootcampIdAndCapacityId(200L, 2L)).thenReturn(Mono.just(entity));

    // When
    Mono<CapacityBootcamp> result = capacityRepositoryAdapter.findByBootcampIdAndCapacityId(200L, 2L);

    // Then
    StepVerifier.create(result)
      .assertNext(cb -> {
        org.assertj.core.api.Assertions.assertThat(cb.getBootcampId().getValue()).isEqualTo(200L);
        org.assertj.core.api.Assertions.assertThat(cb.getCapacityId().getValue()).isEqualTo(2L);
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("Should keep page slice stable and reverse in-memory when order=desc")
  void shouldKeepPageSliceStableAndReverseInMemoryWhenOrderDesc() {
    // Given: DB returns ASC slice for page 0 size 5
    List<CapacityEntity> ascSlice = List.of(
      new CapacityEntity(1L, "Authentication", "Auth description"),
      new CapacityEntity(2L, "Backend", "Backend description"),
      new CapacityEntity(3L, "Content", "Content description"),
      new CapacityEntity(4L, "Data", "Data description"),
      new CapacityEntity(5L, "Engine", "Engine description")
    );

    when(capacityRepository.findAllOrderByNameAsc(5, 0)).thenReturn(Flux.fromIterable(ascSlice));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAllPagedSorted(0, 5, "name", "desc");

    // Then: same 5 elements but reversed order
    StepVerifier.create(result)
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Engine"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Data"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Content"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Backend"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Authentication"))
      .verifyComplete();
  }

  @Test
  @DisplayName("Should return ASC page slice when order=asc")
  void shouldReturnAscPageSliceWhenOrderAsc() {
    // Given
    List<CapacityEntity> ascSlice = List.of(
      new CapacityEntity(1L, "Authentication", "Auth description"),
      new CapacityEntity(2L, "Backend", "Backend description"),
      new CapacityEntity(3L, "Content", "Content description"),
      new CapacityEntity(4L, "Data", "Data description"),
      new CapacityEntity(5L, "Engine", "Engine description")
    );
    when(capacityRepository.findAllOrderByNameAsc(5, 0)).thenReturn(Flux.fromIterable(ascSlice));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAllPagedSorted(0, 5, "name", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Authentication"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Backend"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Content"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Data"))
      .assertNext(c -> assertThat(c.getName().getValue()).isEqualTo("Engine"))
      .verifyComplete();
  }

  @DisplayName("Should return true when capacity exists by name")
  void shouldReturnTrueWhenCapacityExistsByName() {
    // Given
    String capacityName = "Existing Capacity";
    when(capacityRepository.existsByName(capacityName)).thenReturn(Mono.just(true));

    // When
    Mono<Boolean> result = capacityRepositoryAdapter.existsByName(capacityName);

    // Then
    StepVerifier.create(result)
      .expectNext(true)
      .verifyComplete();

    verify(capacityRepository).existsByName(capacityName);
  }

  @Test
  @DisplayName("Should return false when capacity does not exist by name")
  void shouldReturnFalseWhenCapacityDoesNotExistByName() {
    // Given
    String capacityName = "Non-existing Capacity";
    when(capacityRepository.existsByName(capacityName)).thenReturn(Mono.just(false));

    // When
    Mono<Boolean> result = capacityRepositoryAdapter.existsByName(capacityName);

    // Then
    StepVerifier.create(result)
      .expectNext(false)
      .verifyComplete();

    verify(capacityRepository).existsByName(capacityName);
  }

  @Test
  @DisplayName("Should handle error when checking capacity existence by name")
  void shouldHandleErrorWhenCheckingCapacityExistenceByName() {
    // Given
    String capacityName = "Error Capacity";
    RuntimeException error = new RuntimeException("Database error");
    when(capacityRepository.existsByName(capacityName)).thenReturn(Mono.error(error));

    // When
    Mono<Boolean> result = capacityRepositoryAdapter.existsByName(capacityName);

    // Then
    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();

    verify(capacityRepository).existsByName(capacityName);
  }

  @Test
  @DisplayName("Should save capacity successfully and return capacity with generated ID")
  void shouldSaveCapacitySuccessfullyAndReturnCapacityWithGeneratedId() {
    // Given
    Capacity capacityToSave = new Capacity("New Capacity", "New Description");
    CapacityEntity savedEntity = new CapacityEntity(123L, "New Capacity", "New Description");

    when(capacityRepository.save(any(CapacityEntity.class))).thenReturn(Mono.just(savedEntity));

    // When
    Mono<Capacity> result = capacityRepositoryAdapter.save(capacityToSave);

    // Then
    StepVerifier.create(result)
      .assertNext(savedCapacity -> {
        assertThat(savedCapacity.getName().getValue()).isEqualTo("New Capacity");
        assertThat(savedCapacity.getDescription().getValue()).isEqualTo("New Description");
        assertThat(savedCapacity.getId()).isNotNull();
        assertThat(savedCapacity.getId().getValue()).isEqualTo(123L);
      })
      .verifyComplete();

    verify(capacityRepository).save(any(CapacityEntity.class));
  }

  @Test
  @DisplayName("Should save capacity with null ID and set it after save")
  void shouldSaveCapacityWithNullIdAndSetItAfterSave() {
    // Given
    Capacity capacityToSave = new Capacity("Test Capacity", "Test Description");
    // Ensure the capacity has no ID initially
    assertThat(capacityToSave.getId()).isNull();

    CapacityEntity savedEntity = new CapacityEntity(456L, "Test Capacity", "Test Description");
    when(capacityRepository.save(any(CapacityEntity.class))).thenReturn(Mono.just(savedEntity));

    // When
    Mono<Capacity> result = capacityRepositoryAdapter.save(capacityToSave);

    // Then
    StepVerifier.create(result)
      .assertNext(savedCapacity -> {
        assertThat(savedCapacity.getId()).isNotNull();
        assertThat(savedCapacity.getId().getValue()).isEqualTo(456L);
        assertThat(savedCapacity.getName().getValue()).isEqualTo("Test Capacity");
        assertThat(savedCapacity.getDescription().getValue()).isEqualTo("Test Description");
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("Should handle error when saving capacity")
  void shouldHandleErrorWhenSavingCapacity() {
    // Given
    Capacity capacityToSave = new Capacity("Error Capacity", "Error Description");
    RuntimeException error = new RuntimeException("Database save error");
    when(capacityRepository.save(any(CapacityEntity.class))).thenReturn(Mono.error(error));

    // When
    Mono<Capacity> result = capacityRepositoryAdapter.save(capacityToSave);

    // Then
    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();

    verify(capacityRepository).save(any(CapacityEntity.class));
  }

  @Test
  @DisplayName("Should create correct CapacityEntity when saving")
  void shouldCreateCorrectCapacityEntityWhenSaving() {
    // Given
    Capacity capacityToSave = new Capacity("Specific Capacity", "Specific Description");
    CapacityEntity expectedEntity = new CapacityEntity(null, "Specific Capacity", "Specific Description");
    CapacityEntity savedEntity = new CapacityEntity(789L, "Specific Capacity", "Specific Description");

    when(capacityRepository.save(any(CapacityEntity.class))).thenReturn(Mono.just(savedEntity));

    // When
    capacityRepositoryAdapter.save(capacityToSave).block();

    // Then
    verify(capacityRepository).save(any(CapacityEntity.class));
  }

  @Test
  @DisplayName("Should preserve original capacity object reference when saving")
  void shouldPreserveOriginalCapacityObjectReferenceWhenSaving() {
    // Given
    Capacity originalCapacity = new Capacity("Original Capacity", "Original Description");
    CapacityEntity savedEntity = new CapacityEntity(999L, "Original Capacity", "Original Description");

    when(capacityRepository.save(any(CapacityEntity.class))).thenReturn(Mono.just(savedEntity));

    // When
    Mono<Capacity> result = capacityRepositoryAdapter.save(originalCapacity);

    // Then
    StepVerifier.create(result)
      .assertNext(savedCapacity -> {
        // Verify that the returned object is the same reference as the input
        assertThat(savedCapacity).isSameAs(originalCapacity);
        assertThat(savedCapacity.getId().getValue()).isEqualTo(999L);
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("Should handle empty string name when checking existence")
  void shouldHandleEmptyStringNameWhenCheckingExistence() {
    // Given
    String emptyName = "";
    when(capacityRepository.existsByName(emptyName)).thenReturn(Mono.just(false));

    // When
    Mono<Boolean> result = capacityRepositoryAdapter.existsByName(emptyName);

    // Then
    StepVerifier.create(result)
      .expectNext(false)
      .verifyComplete();

    verify(capacityRepository).existsByName(emptyName);
  }

  @Test
  @DisplayName("Should handle null name when checking existence")
  void shouldHandleNullNameWhenCheckingExistence() {
    // Given
    String nullName = null;
    when(capacityRepository.existsByName(nullName)).thenReturn(Mono.just(false));

    // When
    Mono<Boolean> result = capacityRepositoryAdapter.existsByName(nullName);

    // Then
    StepVerifier.create(result)
      .expectNext(false)
      .verifyComplete();

    verify(capacityRepository).existsByName(nullName);
  }

  @Test
  @DisplayName("Should return all capacities successfully")
  void shouldReturnAllCapacitiesSuccessfully() {
    // Given
    CapacityEntity entity1 = new CapacityEntity(1L, "Capacity 1", "Description 1");
    CapacityEntity entity2 = new CapacityEntity(2L, "Capacity 2", "Description 2");
    CapacityEntity entity3 = new CapacityEntity(3L, "Capacity 3", "Description 3");

    when(capacityRepository.findAll()).thenReturn(Flux.just(entity1, entity2, entity3));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAll();

    // Then
    StepVerifier.create(result)
      .assertNext(capacity -> {
        assertThat(capacity.getId().getValue()).isEqualTo(1L);
        assertThat(capacity.getName().getValue()).isEqualTo("Capacity 1");
        assertThat(capacity.getDescription().getValue()).isEqualTo("Description 1");
      })
      .assertNext(capacity -> {
        assertThat(capacity.getId().getValue()).isEqualTo(2L);
        assertThat(capacity.getName().getValue()).isEqualTo("Capacity 2");
        assertThat(capacity.getDescription().getValue()).isEqualTo("Description 2");
      })
      .assertNext(capacity -> {
        assertThat(capacity.getId().getValue()).isEqualTo(3L);
        assertThat(capacity.getName().getValue()).isEqualTo("Capacity 3");
        assertThat(capacity.getDescription().getValue()).isEqualTo("Description 3");
      })
      .verifyComplete();

    verify(capacityRepository).findAll();
  }

  @Test
  @DisplayName("Should return empty flux when no capacities exist")
  void shouldReturnEmptyFluxWhenNoCapacitiesExist() {
    // Given
    when(capacityRepository.findAll()).thenReturn(Flux.empty());

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAll();

    // Then
    StepVerifier.create(result)
      .verifyComplete();

    verify(capacityRepository).findAll();
  }

  @Test
  @DisplayName("Should handle single capacity in findAll")
  void shouldHandleSingleCapacityInFindAll() {
    // Given
    CapacityEntity entity = new CapacityEntity(1L, "Single Capacity", "Single Description");
    when(capacityRepository.findAll()).thenReturn(Flux.just(entity));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAll();

    // Then
    StepVerifier.create(result)
      .assertNext(capacity -> {
        assertThat(capacity.getId().getValue()).isEqualTo(1L);
        assertThat(capacity.getName().getValue()).isEqualTo("Single Capacity");
        assertThat(capacity.getDescription().getValue()).isEqualTo("Single Description");
      })
      .verifyComplete();

    verify(capacityRepository).findAll();
  }

  @Test
  @DisplayName("Should handle capacities with special characters in findAll")
  void shouldHandleCapacitiesWithSpecialCharactersInFindAll() {
    // Given
    CapacityEntity entity1 = new CapacityEntity(1L, "Special Chars: @#$%^&*()", "Description with áéíóú ñ");
    CapacityEntity entity2 = new CapacityEntity(2L, "Another Special: !@#$%", "Another with special chars");

    when(capacityRepository.findAll()).thenReturn(Flux.just(entity1, entity2));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAll();

    // Then
    StepVerifier.create(result)
      .assertNext(capacity -> {
        assertThat(capacity.getId().getValue()).isEqualTo(1L);
        assertThat(capacity.getName().getValue()).isEqualTo("Special Chars: @#$%^&*()");
        assertThat(capacity.getDescription().getValue()).isEqualTo("Description with áéíóú ñ");
      })
      .assertNext(capacity -> {
        assertThat(capacity.getId().getValue()).isEqualTo(2L);
        assertThat(capacity.getName().getValue()).isEqualTo("Another Special: !@#$%");
        assertThat(capacity.getDescription().getValue()).isEqualTo("Another with special chars");
      })
      .verifyComplete();

    verify(capacityRepository).findAll();
  }

  @Test
  @DisplayName("Should handle error when findAll fails")
  void shouldHandleErrorWhenFindAllFails() {
    // Given
    RuntimeException error = new RuntimeException("Database connection failed");
    when(capacityRepository.findAll()).thenReturn(Flux.error(error));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAll();

    // Then
    StepVerifier.create(result)
      .expectError(RuntimeException.class)
      .verify();

    verify(capacityRepository).findAll();
  }

  @Test
  @DisplayName("Should handle large number of capacities in findAll")
  void shouldHandleLargeNumberOfCapacitiesInFindAll() {
    // Given
    int capacityCount = 100;
    List<CapacityEntity> entities = java.util.stream.IntStream.rangeClosed(1, capacityCount)
      .mapToObj(i -> new CapacityEntity((long) i, "Capacity " + i, "Description " + i))
      .toList();

    when(capacityRepository.findAll()).thenReturn(Flux.fromIterable(entities));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAll();

    // Then
    StepVerifier.create(result)
      .expectNextCount(capacityCount)
      .verifyComplete();

    verify(capacityRepository).findAll();
  }

  @Test
  @DisplayName("Should preserve order of capacities in findAll")
  void shouldPreserveOrderOfCapacitiesInFindAll() {
    // Given
    CapacityEntity entity1 = new CapacityEntity(1L, "First Capacity", "First Description");
    CapacityEntity entity2 = new CapacityEntity(2L, "Second Capacity", "Second Description");
    CapacityEntity entity3 = new CapacityEntity(3L, "Third Capacity", "Third Description");

    when(capacityRepository.findAll()).thenReturn(Flux.just(entity1, entity2, entity3));

    // When
    Flux<Capacity> result = capacityRepositoryAdapter.findAll();

    // Then
    StepVerifier.create(result)
      .assertNext(capacity -> assertThat(capacity.getName().getValue()).isEqualTo("First Capacity"))
      .assertNext(capacity -> assertThat(capacity.getName().getValue()).isEqualTo("Second Capacity"))
      .assertNext(capacity -> assertThat(capacity.getName().getValue()).isEqualTo("Third Capacity"))
      .verifyComplete();

    verify(capacityRepository).findAll();
  }
}
