package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.Technology;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.usecase.response.CapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCapacityByBootcampUseCase Tests")
class GetCapacityByBootcampUseCaseTest {

    @Mock
    private CapacityGateway capacityGateway;

    @Mock
    private TechnologyGateway technologyGateway;

    private GetCapacityByBootcampUseCase getCapacityByBootcampUseCase;

    @BeforeEach
    void setUp() {
        getCapacityByBootcampUseCase = new GetCapacityByBootcampUseCase(capacityGateway, technologyGateway);
    }

    @Test
    @DisplayName("Should get capacities by bootcamp successfully with technologies")
    void shouldGetCapacitiesByBootcampSuccessfullyWithTechnologies() {
        // Given
        Long bootcampId = 1L;
        Long capacityId1 = 1L;
        Long capacityId2 = 2L;
        Long techId1 = 1L;
        Long techId2 = 2L;
        Long techId3 = 3L;

        Capacity capacity1 = new Capacity(capacityId1, "Backend Development", "Backend development capacity");
        Capacity capacity2 = new Capacity(capacityId2, "Frontend Development", "Frontend development capacity");

        Technology technology1 = new Technology(techId1, "Java", "Java programming language");
        Technology technology2 = new Technology(techId2, "Spring", "Spring Framework");
        Technology technology3 = new Technology(techId3, "React", "React library");

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.just(capacity1, capacity2));

        when(technologyGateway.findByCapacityId(capacityId1))
            .thenReturn(Flux.just(technology1, technology2));

        when(technologyGateway.findByCapacityId(capacityId2))
            .thenReturn(Flux.just(technology3));

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId1) &&
                response.getName().equals("Backend Development") &&
                response.getDescription().equals("Backend development capacity") &&
                response.getTechnologies().size() == 2 &&
                response.getTechnologies().get(0).getName().equals("Java") &&
                response.getTechnologies().get(1).getName().equals("Spring")
            )
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId2) &&
                response.getName().equals("Frontend Development") &&
                response.getDescription().equals("Frontend development capacity") &&
                response.getTechnologies().size() == 1 &&
                response.getTechnologies().get(0).getName().equals("React")
            )
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway).findByCapacityId(capacityId1);
        verify(technologyGateway).findByCapacityId(capacityId2);
    }

    @Test
    @DisplayName("Should get capacities by bootcamp successfully with empty technologies")
    void shouldGetCapacitiesByBootcampSuccessfullyWithEmptyTechnologies() {
        // Given
        Long bootcampId = 1L;
        Long capacityId = 1L;

        Capacity capacity = new Capacity(capacityId, "Backend Development", "Backend development capacity");

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.just(capacity));

        when(technologyGateway.findByCapacityId(capacityId))
            .thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId) &&
                response.getName().equals("Backend Development") &&
                response.getDescription().equals("Backend development capacity") &&
                response.getTechnologies().isEmpty()
            )
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway).findByCapacityId(capacityId);
    }

    @Test
    @DisplayName("Should return empty flux when no capacities found for bootcamp")
    void shouldReturnEmptyFluxWhenNoCapacitiesFoundForBootcamp() {
        // Given
        Long bootcampId = 999L;

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway, never()).findByCapacityId(anyLong());
    }

    @Test
    @DisplayName("Should handle single capacity with multiple technologies")
    void shouldHandleSingleCapacityWithMultipleTechnologies() {
        // Given
        Long bootcampId = 1L;
        Long capacityId = 1L;

        Capacity capacity = new Capacity(capacityId, "Full Stack Development", "Full stack development capacity");

        Technology technology1 = new Technology(1L, "Java", "Java programming language");
        Technology technology2 = new Technology(2L, "Spring", "Spring Framework");
        Technology technology3 = new Technology(3L, "React", "React library");
        Technology technology4 = new Technology(4L, "PostgreSQL", "PostgreSQL database");

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.just(capacity));

        when(technologyGateway.findByCapacityId(capacityId))
            .thenReturn(Flux.just(technology1, technology2, technology3, technology4));

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId) &&
                response.getName().equals("Full Stack Development") &&
                response.getDescription().equals("Full stack development capacity") &&
                response.getTechnologies().size() == 4 &&
                response.getTechnologies().stream()
                    .anyMatch(tech -> tech.getName().equals("Java")) &&
                response.getTechnologies().stream()
                    .anyMatch(tech -> tech.getName().equals("Spring")) &&
                response.getTechnologies().stream()
                    .anyMatch(tech -> tech.getName().equals("React")) &&
                response.getTechnologies().stream()
                    .anyMatch(tech -> tech.getName().equals("PostgreSQL"))
            )
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway).findByCapacityId(capacityId);
    }

    @Test
    @DisplayName("Should handle multiple capacities with different technology counts")
    void shouldHandleMultipleCapacitiesWithDifferentTechnologyCounts() {
        // Given
        Long bootcampId = 1L;
        Long capacityId1 = 1L;
        Long capacityId2 = 2L;
        Long capacityId3 = 3L;

        Capacity capacity1 = new Capacity(capacityId1, "Backend Development", "Backend development capacity");
        Capacity capacity2 = new Capacity(capacityId2, "Frontend Development", "Frontend development capacity");
        Capacity capacity3 = new Capacity(capacityId3, "DevOps", "DevOps capacity");

        Technology technology1 = new Technology(1L, "Java", "Java programming language");
        Technology technology2 = new Technology(2L, "Spring", "Spring Framework");
        Technology technology3 = new Technology(3L, "React", "React library");
        Technology technology4 = new Technology(4L, "Docker", "Docker containerization");
        Technology technology5 = new Technology(5L, "Kubernetes", "Kubernetes orchestration");

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.just(capacity1, capacity2, capacity3));

        when(technologyGateway.findByCapacityId(capacityId1))
            .thenReturn(Flux.just(technology1, technology2));

        when(technologyGateway.findByCapacityId(capacityId2))
            .thenReturn(Flux.just(technology3));

        when(technologyGateway.findByCapacityId(capacityId3))
            .thenReturn(Flux.just(technology4, technology5));

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId1) &&
                response.getTechnologies().size() == 2
            )
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId2) &&
                response.getTechnologies().size() == 1
            )
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId3) &&
                response.getTechnologies().size() == 2
            )
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway).findByCapacityId(capacityId1);
        verify(technologyGateway).findByCapacityId(capacityId2);
        verify(technologyGateway).findByCapacityId(capacityId3);
    }

    @Test
    @DisplayName("Should verify correct technology response mapping")
    void shouldVerifyCorrectTechnologyResponseMapping() {
        // Given
        Long bootcampId = 1L;
        Long capacityId = 1L;
        Long techId = 1L;

        Capacity capacity = new Capacity(capacityId, "Backend Development", "Backend development capacity");
        Technology technology = new Technology(techId, "Java", "Java programming language");

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.just(capacity));

        when(technologyGateway.findByCapacityId(capacityId))
            .thenReturn(Flux.just(technology));

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> {
                TechnologyResponse techResponse = response.getTechnologies().get(0);
                return techResponse.getTechnologyId().equals(techId) &&
                       techResponse.getName().equals("Java") &&
                       techResponse.getDescription().equals("Java programming language");
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should verify correct capacity response mapping")
    void shouldVerifyCorrectCapacityResponseMapping() {
        // Given
        Long bootcampId = 1L;
        Long capacityId = 1L;

        Capacity capacity = new Capacity(capacityId, "Backend Development", "Backend development capacity");

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.just(capacity));

        when(technologyGateway.findByCapacityId(capacityId))
            .thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.getCapacityId().equals(capacityId) &&
                response.getName().equals("Backend Development") &&
                response.getDescription().equals("Backend development capacity") &&
                response.getTechnologies().isEmpty()
            )
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle null bootcamp ID gracefully")
    void shouldHandleNullBootcampIdGracefully() {
        // Given
        Long bootcampId = null;

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway, never()).findByCapacityId(anyLong());
    }

    @Test
    @DisplayName("Should handle zero bootcamp ID")
    void shouldHandleZeroBootcampId() {
        // Given
        Long bootcampId = 0L;

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway, never()).findByCapacityId(anyLong());
    }

    @Test
    @DisplayName("Should handle negative bootcamp ID")
    void shouldHandleNegativeBootcampId() {
        // Given
        Long bootcampId = -1L;

        when(capacityGateway.findByBootcamp(bootcampId))
            .thenReturn(Flux.empty());

        // When
        Flux<CapacityResponse> result = getCapacityByBootcampUseCase.execute(bootcampId);

        // Then
        StepVerifier.create(result)
            .verifyComplete();

        verify(capacityGateway).findByBootcamp(bootcampId);
        verify(technologyGateway, never()).findByCapacityId(anyLong());
    }
}
