package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnology;
import co.com.bancolombia.model.capacity.Technology;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.usecase.command.CreateCapacityCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCapacityUseCase Tests")
class CreateCapacityUseCaseTest {

    @Mock
    private CapacityGateway capacityGateway;

    @Mock
    private TechnologyGateway technologyGateway;

    private CreateCapacityUseCase createCapacityUseCase;

    @BeforeEach
    void setUp() {
        createCapacityUseCase = new CreateCapacityUseCase(capacityGateway, technologyGateway);
    }

    @Test
    @DisplayName("Should create capacity successfully with valid data")
    void shouldCreateCapacitySuccessfullyWithValidData() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        Capacity savedCapacity = new Capacity(capacityName, capacityDescription);
        savedCapacity.setId(new Id(1L));

        Technology javaTech = new Technology(1L, "Java", "Java programming language");
        Technology springTech = new Technology(2L, "Spring", "Spring Framework");
        Technology postgresTech = new Technology(3L, "PostgreSQL", "PostgreSQL database");

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(false));
        when(capacityGateway.save(any(Capacity.class))).thenReturn(Mono.just(savedCapacity));
        when(technologyGateway.findAll()).thenReturn(Flux.just(javaTech, springTech, postgresTech));
        when(technologyGateway.associateTechnology(any(CapacityTechnology.class)))
            .thenReturn(Mono.just(javaTech))
            .thenReturn(Mono.just(springTech))
            .thenReturn(Mono.just(postgresTech));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals(1L, response.getCapacityId());
                assertEquals(capacityName, response.getName());
                assertEquals(capacityDescription, response.getDescription());
                assertEquals(3, response.getTechnologies().size());
                
                List<String> responseTechNames = response.getTechnologies().stream()
                    .map(TechnologyResponse::getName)
                    .toList();
                assertTrue(responseTechNames.containsAll(technologyNames));
            })
            .verifyComplete();

        verify(capacityGateway).existsByName(capacityName);
        verify(capacityGateway).save(any(Capacity.class));
        verify(technologyGateway).findAll();
        verify(technologyGateway, times(3)).associateTechnology(any(CapacityTechnology.class));
    }

    @Test
    @DisplayName("Should throw exception when capacity has less than 3 technologies")
    void shouldThrowExceptionWhenCapacityHasLessThanThreeTechnologies() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals("The capacity should have 3 technologies minimum.")
            )
            .verify();

        verify(capacityGateway, never()).existsByName(anyString());
        verify(capacityGateway, never()).save(any(Capacity.class));
        verify(technologyGateway, never()).findAll();
    }

    @Test
    @DisplayName("Should throw exception when capacity has more than 20 technologies")
    void shouldThrowExceptionWhenCapacityHasMoreThanTwentyTechnologies() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList(
            "Java", "Spring", "PostgreSQL", "Docker", "Kubernetes",
            "React", "Angular", "Vue", "Node.js", "Python",
            "Go", "Rust", "C#", "PHP", "Ruby",
            "MySQL", "MongoDB", "Redis", "Elasticsearch", "RabbitMQ",
            "Extra Technology"
        );
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals("The capacity should have 20 technologies maximum.")
            )
            .verify();

        verify(capacityGateway, never()).existsByName(anyString());
        verify(capacityGateway, never()).save(any(Capacity.class));
        verify(technologyGateway, never()).findAll();
    }

    @Test
    @DisplayName("Should throw exception when technologies are duplicated")
    void shouldThrowExceptionWhenTechnologiesAreDuplicated() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "Java");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals("The capacity should not have duplicated technologies.")
            )
            .verify();

        verify(capacityGateway, never()).existsByName(anyString());
        verify(capacityGateway, never()).save(any(Capacity.class));
        verify(technologyGateway, never()).findAll();
    }

    @Test
    @DisplayName("Should throw exception when capacity name already exists")
    void shouldThrowExceptionWhenCapacityNameAlreadyExists() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals("The capacity name cannot be duplicated.")
            )
            .verify();

        verify(capacityGateway).existsByName(capacityName);
        verify(capacityGateway, never()).save(any(Capacity.class));
        verify(technologyGateway, never()).findAll();
    }

    @Test
    @DisplayName("Should throw exception when some technologies do not exist")
    void shouldThrowExceptionWhenSomeTechnologiesDoNotExist() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "NonExistentTech");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        Technology javaTech = new Technology(1L, "Java", "Java programming language");
        Technology springTech = new Technology(2L, "Spring", "Spring Framework");

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(false));
        when(technologyGateway.findAll()).thenReturn(Flux.just(javaTech, springTech));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals("Some technologies have not been found.")
            )
            .verify();

        verify(capacityGateway).existsByName(capacityName);
        verify(technologyGateway).findAll();
        verify(capacityGateway, never()).save(any(Capacity.class));
        verify(technologyGateway, never()).associateTechnology(any(CapacityTechnology.class));
    }

    @Test
    @DisplayName("Should handle capacity gateway error when checking name existence")
    void shouldHandleCapacityGatewayErrorWhenCheckingNameExistence() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        when(capacityGateway.existsByName(capacityName))
            .thenReturn(Mono.error(new RuntimeException("Database connection error")));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(capacityGateway).existsByName(capacityName);
        verify(capacityGateway, never()).save(any(Capacity.class));
        verify(technologyGateway, never()).findAll();
    }

    @Test
    @DisplayName("Should handle technology gateway error when finding all technologies")
    void shouldHandleTechnologyGatewayErrorWhenFindingAllTechnologies() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(false));
        when(technologyGateway.findAll())
            .thenReturn(Flux.error(new RuntimeException("Database connection error")));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(capacityGateway).existsByName(capacityName);
        verify(technologyGateway).findAll();
        verify(capacityGateway, never()).save(any(Capacity.class));
    }

    @Test
    @DisplayName("Should handle capacity gateway error when saving capacity")
    void shouldHandleCapacityGatewayErrorWhenSavingCapacity() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        Technology javaTech = new Technology(1L, "Java", "Java programming language");
        Technology springTech = new Technology(2L, "Spring", "Spring Framework");
        Technology postgresTech = new Technology(3L, "PostgreSQL", "PostgreSQL database");

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(false));
        when(technologyGateway.findAll()).thenReturn(Flux.just(javaTech, springTech, postgresTech));
        when(capacityGateway.save(any(Capacity.class)))
            .thenReturn(Mono.error(new RuntimeException("Database save error")));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(capacityGateway).existsByName(capacityName);
        verify(technologyGateway).findAll();
        verify(capacityGateway).save(any(Capacity.class));
        verify(technologyGateway, never()).associateTechnology(any(CapacityTechnology.class));
    }

    @Test
    @DisplayName("Should handle technology gateway error when associating technology")
    void shouldHandleTechnologyGatewayErrorWhenAssociatingTechnology() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        Capacity savedCapacity = new Capacity(capacityName, capacityDescription);
        savedCapacity.setId(new Id(1L));

        Technology javaTech = new Technology(1L, "Java", "Java programming language");
        Technology springTech = new Technology(2L, "Spring", "Spring Framework");
        Technology postgresTech = new Technology(3L, "PostgreSQL", "PostgreSQL database");

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(false));
        when(capacityGateway.save(any(Capacity.class))).thenReturn(Mono.just(savedCapacity));
        when(technologyGateway.findAll()).thenReturn(Flux.just(javaTech, springTech, postgresTech));
        when(technologyGateway.associateTechnology(any(CapacityTechnology.class)))
            .thenReturn(Mono.just(javaTech))
            .thenReturn(Mono.error(new RuntimeException("Association error")));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectError(RuntimeException.class)
            .verify();

        verify(capacityGateway).existsByName(capacityName);
        verify(capacityGateway).save(any(Capacity.class));
        verify(technologyGateway).findAll();
        verify(technologyGateway, atLeastOnce()).associateTechnology(any(CapacityTechnology.class));
    }

    @Test
    @DisplayName("Should validate minimum technologies boundary condition")
    void shouldValidateMinimumTechnologiesBoundaryCondition() {
        // Given - exactly 3 technologies (minimum allowed)
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        Capacity savedCapacity = new Capacity(capacityName, capacityDescription);
        savedCapacity.setId(new Id(1L));

        Technology javaTech = new Technology(1L, "Java", "Java programming language");
        Technology springTech = new Technology(2L, "Spring", "Spring Framework");
        Technology postgresTech = new Technology(3L, "PostgreSQL", "PostgreSQL database");

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(false));
        when(capacityGateway.save(any(Capacity.class))).thenReturn(Mono.just(savedCapacity));
        when(technologyGateway.findAll()).thenReturn(Flux.just(javaTech, springTech, postgresTech));
        when(technologyGateway.associateTechnology(any(CapacityTechnology.class)))
            .thenReturn(Mono.just(javaTech))
            .thenReturn(Mono.just(springTech))
            .thenReturn(Mono.just(postgresTech));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals(3, response.getTechnologies().size());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should validate maximum technologies boundary condition")
    void shouldValidateMaximumTechnologiesBoundaryCondition() {
        // Given - exactly 20 technologies (maximum allowed)
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList(
            "Tech1", "Tech2", "Tech3", "Tech4", "Tech5",
            "Tech6", "Tech7", "Tech8", "Tech9", "Tech10",
            "Tech11", "Tech12", "Tech13", "Tech14", "Tech15",
            "Tech16", "Tech17", "Tech18", "Tech19", "Tech20"
        );
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        Capacity savedCapacity = new Capacity(capacityName, capacityDescription);
        savedCapacity.setId(new Id(1L));

        // Create 20 mock technologies
        List<Technology> technologies = technologyNames.stream()
            .map(name -> new Technology(1L, name, "Description for " + name))
            .toList();

        when(capacityGateway.existsByName(capacityName)).thenReturn(Mono.just(false));
        when(capacityGateway.save(any(Capacity.class))).thenReturn(Mono.just(savedCapacity));
        when(technologyGateway.findAll()).thenReturn(Flux.fromIterable(technologies));
        when(technologyGateway.associateTechnology(any(CapacityTechnology.class)))
            .thenReturn(Mono.just(technologies.get(0)));

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals(20, response.getTechnologies().size());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle empty technology list")
    void shouldHandleEmptyTechnologyList() {
        // Given
        String capacityName = "Backend Development";
        String capacityDescription = "Backend development capacity";
        List<String> technologyNames = Arrays.asList();
        
        CreateCapacityCommand command = new CreateCapacityCommand(
            capacityName, capacityDescription, technologyNames
        );

        // When & Then
        StepVerifier.create(createCapacityUseCase.execute(command))
            .expectErrorMatches(throwable -> 
                throwable instanceof BussinessException &&
                throwable.getMessage().equals("The capacity should have 3 technologies minimum.")
            )
            .verify();
    }
}
