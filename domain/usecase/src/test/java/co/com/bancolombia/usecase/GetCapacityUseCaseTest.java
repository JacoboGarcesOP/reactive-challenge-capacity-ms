package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.Technology;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import co.com.bancolombia.usecase.response.CapacityResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCapacityUseCase Tests")
class GetCapacityUseCaseTest {

    @Mock
    private CapacityGateway capacityGateway;

    @Mock
    private TechnologyGateway technologyGateway;

    private GetCapacityUseCase getCapacityUseCase;

    @BeforeEach
    void setUp() {
        getCapacityUseCase = new GetCapacityUseCase(capacityGateway, technologyGateway);
    }

    @Test
    @DisplayName("Should return all capacities with their technologies successfully")
    void shouldReturnAllCapacitiesWithTheirTechnologiesSuccessfully() {
        // Given
        Capacity capacity1 = new Capacity(1L, "Payments Squad", "Handles payment features");
        Capacity capacity2 = new Capacity(2L, "User Management", "Handles user operations");

        Technology tech1 = new Technology(1L, "Java", "Java Programming Language");
        Technology tech2 = new Technology(2L, "Spring Boot", "Spring Boot Framework");
        Technology tech3 = new Technology(3L, "PostgreSQL", "PostgreSQL Database");
        Technology tech4 = new Technology(4L, "React", "React Framework");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity1, capacity2));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.just(tech1, tech2));
        when(technologyGateway.findByCapacityId(2L)).thenReturn(Flux.just(tech3, tech4));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("Payments Squad");
                    assertThat(capacityResponse.getDescription()).isEqualTo("Handles payment features");
                    assertThat(capacityResponse.getTechnologies()).hasSize(2);
                    assertThat(capacityResponse.getTechnologies().get(0).getTechnologyId()).isEqualTo(1L);
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("Java");
                    assertThat(capacityResponse.getTechnologies().get(0).getDescription()).isEqualTo("Java Programming Language");
                    assertThat(capacityResponse.getTechnologies().get(1).getTechnologyId()).isEqualTo(2L);
                    assertThat(capacityResponse.getTechnologies().get(1).getName()).isEqualTo("Spring Boot");
                    assertThat(capacityResponse.getTechnologies().get(1).getDescription()).isEqualTo("Spring Boot Framework");
                })
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(2L);
                    assertThat(capacityResponse.getName()).isEqualTo("User Management");
                    assertThat(capacityResponse.getDescription()).isEqualTo("Handles user operations");
                    assertThat(capacityResponse.getTechnologies()).hasSize(2);
                    assertThat(capacityResponse.getTechnologies().get(0).getTechnologyId()).isEqualTo(3L);
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("PostgreSQL");
                    assertThat(capacityResponse.getTechnologies().get(0).getDescription()).isEqualTo("PostgreSQL Database");
                    assertThat(capacityResponse.getTechnologies().get(1).getTechnologyId()).isEqualTo(4L);
                    assertThat(capacityResponse.getTechnologies().get(1).getName()).isEqualTo("React");
                    assertThat(capacityResponse.getTechnologies().get(1).getDescription()).isEqualTo("React Framework");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty flux when no capacities exist")
    void shouldReturnEmptyFluxWhenNoCapacitiesExist() {
        // Given
        when(capacityGateway.findAll()).thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return capacity with empty technologies when no technologies exist")
    void shouldReturnCapacityWithEmptyTechnologiesWhenNoTechnologiesExist() {
        // Given
        Capacity capacity = new Capacity(1L, "Empty Tech Capacity", "Capacity without technologies");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("Empty Tech Capacity");
                    assertThat(capacityResponse.getDescription()).isEqualTo("Capacity without technologies");
                    assertThat(capacityResponse.getTechnologies()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return capacity with single technology")
    void shouldReturnCapacityWithSingleTechnology() {
        // Given
        Capacity capacity = new Capacity(1L, "Single Tech Capacity", "Capacity with one technology");
        Technology technology = new Technology(1L, "Java", "Java Programming Language");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.just(technology));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("Single Tech Capacity");
                    assertThat(capacityResponse.getDescription()).isEqualTo("Capacity with one technology");
                    assertThat(capacityResponse.getTechnologies()).hasSize(1);
                    assertThat(capacityResponse.getTechnologies().get(0).getTechnologyId()).isEqualTo(1L);
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("Java");
                    assertThat(capacityResponse.getTechnologies().get(0).getDescription()).isEqualTo("Java Programming Language");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return capacity with multiple technologies")
    void shouldReturnCapacityWithMultipleTechnologies() {
        // Given
        Capacity capacity = new Capacity(1L, "Multi Tech Capacity", "Capacity with multiple technologies");
        Technology tech1 = new Technology(1L, "Java", "Java Programming Language");
        Technology tech2 = new Technology(2L, "Spring Boot", "Spring Boot Framework");
        Technology tech3 = new Technology(3L, "PostgreSQL", "PostgreSQL Database");
        Technology tech4 = new Technology(4L, "Docker", "Docker Containerization");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.just(tech1, tech2, tech3, tech4));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("Multi Tech Capacity");
                    assertThat(capacityResponse.getDescription()).isEqualTo("Capacity with multiple technologies");
                    assertThat(capacityResponse.getTechnologies()).hasSize(4);
                    
                    // Verify all technologies are present
                    List<String> techNames = capacityResponse.getTechnologies().stream()
                            .map(TechnologyResponse::getName)
                            .toList();
                    assertThat(techNames).containsExactly("Java", "Spring Boot", "PostgreSQL", "Docker");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle capacity with special characters in name and description")
    void shouldHandleCapacityWithSpecialCharactersInNameAndDescription() {
        // Given
        Capacity capacity = new Capacity(1L, "Special Chars: @#$%^&*()", "Description with áéíóú ñ");
        Technology technology = new Technology(1L, "Java 21", "Java 21 Programming Language");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.just(technology));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("Special Chars: @#$%^&*()");
                    assertThat(capacityResponse.getDescription()).isEqualTo("Description with áéíóú ñ");
                    assertThat(capacityResponse.getTechnologies()).hasSize(1);
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("Java 21");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle technology with special characters in name and description")
    void shouldHandleTechnologyWithSpecialCharactersInNameAndDescription() {
        // Given
        Capacity capacity = new Capacity(1L, "Tech Special Chars", "Testing special characters in technologies");
        Technology technology = new Technology(1L, "Spring-Boot 3.0", "Spring Boot 3.0 Framework with @Value annotations");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.just(technology));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("Tech Special Chars");
                    assertThat(capacityResponse.getDescription()).isEqualTo("Testing special characters in technologies");
                    assertThat(capacityResponse.getTechnologies()).hasSize(1);
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("Spring-Boot 3.0");
                    assertThat(capacityResponse.getTechnologies().get(0).getDescription()).isEqualTo("Spring Boot 3.0 Framework with @Value annotations");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle multiple capacities with different technology counts")
    void shouldHandleMultipleCapacitiesWithDifferentTechnologyCounts() {
        // Given
        Capacity capacity1 = new Capacity(1L, "No Tech Capacity", "Capacity without technologies");
        Capacity capacity2 = new Capacity(2L, "Single Tech Capacity", "Capacity with one technology");
        Capacity capacity3 = new Capacity(3L, "Multi Tech Capacity", "Capacity with multiple technologies");

        Technology tech1 = new Technology(1L, "Java", "Java Programming Language");
        Technology tech2 = new Technology(2L, "Spring Boot", "Spring Boot Framework");
        Technology tech3 = new Technology(3L, "PostgreSQL", "PostgreSQL Database");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity1, capacity2, capacity3));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.empty());
        when(technologyGateway.findByCapacityId(2L)).thenReturn(Flux.just(tech1));
        when(technologyGateway.findByCapacityId(3L)).thenReturn(Flux.just(tech2, tech3));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("No Tech Capacity");
                    assertThat(capacityResponse.getTechnologies()).isEmpty();
                })
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(2L);
                    assertThat(capacityResponse.getName()).isEqualTo("Single Tech Capacity");
                    assertThat(capacityResponse.getTechnologies()).hasSize(1);
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("Java");
                })
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(3L);
                    assertThat(capacityResponse.getName()).isEqualTo("Multi Tech Capacity");
                    assertThat(capacityResponse.getTechnologies()).hasSize(2);
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("Spring Boot");
                    assertThat(capacityResponse.getTechnologies().get(1).getName()).isEqualTo("PostgreSQL");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle error when capacity gateway fails")
    void shouldHandleErrorWhenCapacityGatewayFails() {
        // Given
        RuntimeException error = new RuntimeException("Database connection failed");
        when(capacityGateway.findAll()).thenReturn(Flux.error(error));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle error when technology gateway fails")
    void shouldHandleErrorWhenTechnologyGatewayFails() {
        // Given
        Capacity capacity = new Capacity(1L, "Test Capacity", "Test Description");
        RuntimeException error = new RuntimeException("External service error");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.error(error));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle large number of capacities efficiently")
    void shouldHandleLargeNumberOfCapacitiesEfficiently() {
        // Given
        int capacityCount = 100;
        List<Capacity> capacities = java.util.stream.IntStream.rangeClosed(1, capacityCount)
                .mapToObj(i -> new Capacity((long) i, "Capacity " + i, "Description " + i))
                .toList();

        when(capacityGateway.findAll()).thenReturn(Flux.fromIterable(capacities));
        when(technologyGateway.findByCapacityId(anyLong())).thenReturn(Flux.just(
                new Technology(1L, "Java", "Java Programming Language")
        ));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectNextCount(capacityCount)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle large number of technologies per capacity efficiently")
    void shouldHandleLargeNumberOfTechnologiesPerCapacityEfficiently() {
        // Given
        Capacity capacity = new Capacity(1L, "Large Tech Capacity", "Capacity with many technologies");
        int techCount = 50;
        List<Technology> technologies = java.util.stream.IntStream.rangeClosed(1, techCount)
                .mapToObj(i -> new Technology((long) i, "Technology " + i, "Description " + i))
                .toList();

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.fromIterable(technologies));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("Large Tech Capacity");
                    assertThat(capacityResponse.getTechnologies()).hasSize(techCount);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should preserve order of capacities and technologies")
    void shouldPreserveOrderOfCapacitiesAndTechnologies() {
        // Given
        Capacity capacity1 = new Capacity(1L, "First Capacity", "First Description");
        Capacity capacity2 = new Capacity(2L, "Second Capacity", "Second Description");
        Technology tech1 = new Technology(1L, "First Tech", "First Tech Description");
        Technology tech2 = new Technology(2L, "Second Tech", "Second Tech Description");

        when(capacityGateway.findAll()).thenReturn(Flux.just(capacity1, capacity2));
        when(technologyGateway.findByCapacityId(1L)).thenReturn(Flux.just(tech1, tech2));
        when(technologyGateway.findByCapacityId(2L)).thenReturn(Flux.just(tech2, tech1));

        // When
        Flux<CapacityResponse> result = getCapacityUseCase.execute();

        // Then
        StepVerifier.create(result)
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(1L);
                    assertThat(capacityResponse.getName()).isEqualTo("First Capacity");
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("First Tech");
                    assertThat(capacityResponse.getTechnologies().get(1).getName()).isEqualTo("Second Tech");
                })
                .assertNext(capacityResponse -> {
                    assertThat(capacityResponse.getCapacityId()).isEqualTo(2L);
                    assertThat(capacityResponse.getName()).isEqualTo("Second Capacity");
                    assertThat(capacityResponse.getTechnologies().get(0).getName()).isEqualTo("Second Tech");
                    assertThat(capacityResponse.getTechnologies().get(1).getName()).isEqualTo("First Tech");
                })
                .verifyComplete();
    }
}
