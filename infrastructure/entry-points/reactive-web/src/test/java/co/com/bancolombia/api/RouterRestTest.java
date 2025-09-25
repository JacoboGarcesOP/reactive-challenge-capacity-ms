package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateCapacityRequest;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.Technology;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import co.com.bancolombia.model.capacity.gateway.TechnologyGateway;
import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import co.com.bancolombia.usecase.CreateCapacityUseCase;
import co.com.bancolombia.usecase.GetCapacityUseCase;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.CapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@DisplayName("RouterRest Tests")
class RouterRestTest {

    @Mock
    private CreateCapacityUseCase createCapacityUseCase;

    @Mock
    private GetCapacityUseCase getCapacityUseCase;

    @Mock
    private Validator validator;

    @Mock
    private CapacityGateway capacityGateway;

    @Mock
    private TechnologyGateway technologyGateway;

    private RouterRest routerRest;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        routerRest = new RouterRest();
        Handler handler = new Handler(createCapacityUseCase, getCapacityUseCase, validator);
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    @DisplayName("Should create router function with correct configuration")
    void shouldCreateRouterFunctionWithCorrectConfiguration() {
        // When
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(new Handler(createCapacityUseCase, getCapacityUseCase, validator));

        // Then
        assert routerFunction != null;
    }

    @Test
    @DisplayName("Should accept POST requests to /v1/api/capacity endpoint")
    void shouldAcceptPostRequestsToCapacityEndpoint() {
        // Given
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Test Capacity",
                "Test Description",
                List.of("Java", "Spring Boot", "PostgreSQL")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Test Capacity",
                "Test Description",
                List.of(
                        new TechnologyResponse(1L, "Java", "Java Programming Language"),
                        new TechnologyResponse(2L, "Spring Boot", "Spring Boot Framework"),
                        new TechnologyResponse(3L, "PostgreSQL", "PostgreSQL Database")
                )
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.capacityId").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Capacity")
                .jsonPath("$.description").isEqualTo("Test Description")
                .jsonPath("$.technologies").isArray()
                .jsonPath("$.technologies.length()").isEqualTo(3);
    }

    @Test
    @DisplayName("Should accept GET requests to /v1/api/capacity endpoint")
    void shouldAcceptGetRequestsToCapacityEndpoint() {
        // Given
        CapacityResponse response1 = new CapacityResponse(
                1L,
                "Test Capacity 1",
                "Test Description 1",
                List.of(
                        new TechnologyResponse(1L, "Java", "Java Programming Language"),
                        new TechnologyResponse(2L, "Spring Boot", "Spring Boot Framework")
                )
        );

        CapacityResponse response2 = new CapacityResponse(
                2L,
                "Test Capacity 2",
                "Test Description 2",
                List.of(
                        new TechnologyResponse(3L, "PostgreSQL", "PostgreSQL Database")
                )
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response1, response2));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].capacityId").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Capacity 1")
                .jsonPath("$[0].technologies.length()").isEqualTo(2)
                .jsonPath("$[1].capacityId").isEqualTo(2)
                .jsonPath("$[1].name").isEqualTo("Test Capacity 2")
                .jsonPath("$[1].technologies.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("Should reject PUT requests to /v1/api/capacity endpoint")
    void shouldRejectPutRequestsToCapacityEndpoint() {
        // When & Then
        webTestClient
                .put()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should reject DELETE requests to /v1/api/capacity endpoint")
    void shouldRejectDeleteRequestsToCapacityEndpoint() {
        // When & Then
        webTestClient
                .delete()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should reject PATCH requests to /v1/api/capacity endpoint")
    void shouldRejectPatchRequestsToCapacityEndpoint() {
        // When & Then
        webTestClient
                .patch()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle valid JSON request with minimum technologies")
    void shouldHandleValidJsonRequestWithMinimumTechnologies() {
        // Given
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Minimal Capacity",
                "Minimal description",
                List.of("Java", "Spring", "PostgreSQL")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Minimal Capacity",
                "Minimal description",
                List.of(
                        new TechnologyResponse(1L, "Java", "Java Programming Language"),
                        new TechnologyResponse(2L, "Spring", "Spring Framework"),
                        new TechnologyResponse(3L, "PostgreSQL", "PostgreSQL Database")
                )
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.capacityId").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Minimal Capacity")
                .jsonPath("$.description").isEqualTo("Minimal description")
                .jsonPath("$.technologies.length()").isEqualTo(3);
    }

    @Test
    @DisplayName("Should handle valid JSON request with maximum technologies")
    void shouldHandleValidJsonRequestWithMaximumTechnologies() {
        // Given
        List<String> maxTechs = List.of("Java", "Spring", "PostgreSQL", "Docker", "Kubernetes", "Redis", "MongoDB", "Elasticsearch", "RabbitMQ", "Kafka", "Prometheus", "Grafana", "Jenkins", "GitLab", "SonarQube", "JUnit", "Mockito", "TestContainers", "WireMock", "Cucumber");
        
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Max Capacity",
                "Maximum technologies description",
                maxTechs
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Max Capacity",
                "Maximum technologies description",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.capacityId").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Max Capacity")
                .jsonPath("$.description").isEqualTo("Maximum technologies description");
    }

    @Test
    @DisplayName("Should handle request with special characters in capacity name")
    void shouldHandleRequestWithSpecialCharactersInCapacityName() {
        // Given
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Capacity with Special Chars: @#$%^&*()",
                "Description with special chars: áéíóú ñ",
                List.of("Java", "Spring", "PostgreSQL")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Capacity with Special Chars: @#$%^&*()",
                "Description with special chars: áéíóú ñ",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo("Capacity with Special Chars: @#$%^&*()")
                .jsonPath("$.description").isEqualTo("Description with special chars: áéíóú ñ");
    }

    @Test
    @DisplayName("Should handle request with very long capacity name")
    void shouldHandleRequestWithVeryLongCapacityName() {
        // Given
        String longName = "A".repeat(100);
        CreateCapacityRequest request = new CreateCapacityRequest(
                longName,
                "Description for long name",
                List.of("Java", "Spring", "PostgreSQL")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                longName,
                "Description for long name",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(longName);
    }

    @Test
    @DisplayName("Should handle request with very long description")
    void shouldHandleRequestWithVeryLongDescription() {
        // Given
        String longDescription = "A".repeat(500);
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Long Description Test",
                longDescription,
                List.of("Java", "Spring", "PostgreSQL")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Long Description Test",
                longDescription,
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.description").isEqualTo(longDescription);
    }

    @Test
    @DisplayName("Should handle request with technology names containing special characters")
    void shouldHandleRequestWithTechnologyNamesContainingSpecialCharacters() {
        // Given
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Special Tech Test",
                "Testing special characters in technology names",
                List.of("Java 21", "Spring-Boot", "PostgreSQL 15", "Docker & Kubernetes", "Redis-Cache")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Special Tech Test",
                "Testing special characters in technology names",
                List.of(new TechnologyResponse(1L, "Java 21", "Java 21 Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo("Special Tech Test");
    }

    @Test
    @DisplayName("Should handle request with duplicate technology names")
    void shouldHandleRequestWithDuplicateTechnologyNames() {
        // Given
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Duplicate Tech Test",
                "Testing duplicate technology names",
                List.of("Java", "Spring", "Java", "PostgreSQL", "Spring")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Duplicate Tech Test",
                "Testing duplicate technology names",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo("Duplicate Tech Test");
    }

    @Test
    @DisplayName("Should handle request with empty body")
    void shouldHandleRequestWithEmptyBody() {
        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Should handle request with invalid JSON")
    void shouldHandleRequestWithInvalidJson() {
        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ invalid json }")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Should handle request with different HTTP headers")
    void shouldHandleRequestWithDifferentHttpHeaders() {
        // Given
        CreateCapacityRequest request = new CreateCapacityRequest(
                "Headers Test",
                "Testing different HTTP headers",
                List.of("Java", "Spring", "PostgreSQL")
        );

        CapacityResponse response = new CapacityResponse(
                1L,
                "Headers Test",
                "Testing different HTTP headers",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Custom-Header", "CustomValue")
                .header("User-Agent", "TestAgent/1.0")
                .header("Accept", "application/json")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle concurrent requests to the same endpoint")
    void shouldHandleConcurrentRequestsToTheSameEndpoint() {
        // Given
        CreateCapacityRequest request1 = new CreateCapacityRequest(
                "Concurrent Test 1",
                "First concurrent request",
                List.of("Java", "Spring", "PostgreSQL")
        );

        CreateCapacityRequest request2 = new CreateCapacityRequest(
                "Concurrent Test 2",
                "Second concurrent request",
                List.of("Python", "Django", "MySQL")
        );

        CapacityResponse response1 = new CapacityResponse(
                1L,
                "Concurrent Test 1",
                "First concurrent request",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        CapacityResponse response2 = new CapacityResponse(
                2L,
                "Concurrent Test 2",
                "Second concurrent request",
                List.of(new TechnologyResponse(2L, "Python", "Python Programming Language"))
        );

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response1), Mono.just(response2));

        // When & Then - Both requests should be handled
        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Concurrent Test 1");

        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request2)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Concurrent Test 2");
    }

    @Test
    @DisplayName("Should verify router function is properly configured")
    void shouldVerifyRouterFunctionIsProperlyConfigured() {
        // When
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(new Handler(createCapacityUseCase, getCapacityUseCase, validator));

        // Then
        assert routerFunction != null;
    }

    @Test
    @DisplayName("Should verify base URL constant is correct")
    void shouldVerifyBaseUrlConstantIsCorrect() {
        // This test verifies that the BASE_URL constant is accessible
        // Since it's private, we test it indirectly through the router behavior
        CreateCapacityRequest request = new CreateCapacityRequest("Test", "Test", List.of("Java"));
        CapacityResponse response = new CapacityResponse(1L, "Test", "Test", List.of());

        when(createCapacityUseCase.execute(any())).thenReturn(Mono.just(response));

        webTestClient
                .post()
                .uri("/v1/api/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    // ========== GET /v1/api/capacity Tests ==========

    @Test
    @DisplayName("Should return empty list when no capacities exist")
    void shouldReturnEmptyListWhenNoCapacitiesExist() {
        // Given
        when(getCapacityUseCase.execute()).thenReturn(Flux.empty());

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("Should return single capacity with technologies")
    void shouldReturnSingleCapacityWithTechnologies() {
        // Given
        CapacityResponse response = new CapacityResponse(
                1L,
                "Single Capacity",
                "Single capacity description",
                List.of(
                        new TechnologyResponse(1L, "Java", "Java Programming Language"),
                        new TechnologyResponse(2L, "Spring Boot", "Spring Boot Framework"),
                        new TechnologyResponse(3L, "PostgreSQL", "PostgreSQL Database")
                )
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].capacityId").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Single Capacity")
                .jsonPath("$[0].description").isEqualTo("Single capacity description")
                .jsonPath("$[0].technologies").isArray()
                .jsonPath("$[0].technologies.length()").isEqualTo(3)
                .jsonPath("$[0].technologies[0].technologyId").isEqualTo(1)
                .jsonPath("$[0].technologies[0].name").isEqualTo("Java")
                .jsonPath("$[0].technologies[0].description").isEqualTo("Java Programming Language");
    }

    @Test
    @DisplayName("Should return multiple capacities with different technology counts")
    void shouldReturnMultipleCapacitiesWithDifferentTechnologyCounts() {
        // Given
        CapacityResponse response1 = new CapacityResponse(
                1L,
                "Backend Capacity",
                "Backend development capacity",
                List.of(
                        new TechnologyResponse(1L, "Java", "Java Programming Language"),
                        new TechnologyResponse(2L, "Spring Boot", "Spring Boot Framework"),
                        new TechnologyResponse(3L, "PostgreSQL", "PostgreSQL Database"),
                        new TechnologyResponse(4L, "Docker", "Docker Containerization")
                )
        );

        CapacityResponse response2 = new CapacityResponse(
                2L,
                "Frontend Capacity",
                "Frontend development capacity",
                List.of(
                        new TechnologyResponse(5L, "React", "React Framework"),
                        new TechnologyResponse(6L, "TypeScript", "TypeScript Language")
                )
        );

        CapacityResponse response3 = new CapacityResponse(
                3L,
                "DevOps Capacity",
                "DevOps and infrastructure capacity",
                List.of(
                        new TechnologyResponse(7L, "Kubernetes", "Kubernetes Orchestration"),
                        new TechnologyResponse(8L, "Jenkins", "Jenkins CI/CD"),
                        new TechnologyResponse(9L, "Terraform", "Terraform Infrastructure"),
                        new TechnologyResponse(10L, "Prometheus", "Prometheus Monitoring"),
                        new TechnologyResponse(11L, "Grafana", "Grafana Visualization")
                )
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response1, response2, response3));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].name").isEqualTo("Backend Capacity")
                .jsonPath("$[0].technologies.length()").isEqualTo(4)
                .jsonPath("$[1].name").isEqualTo("Frontend Capacity")
                .jsonPath("$[1].technologies.length()").isEqualTo(2)
                .jsonPath("$[2].name").isEqualTo("DevOps Capacity")
                .jsonPath("$[2].technologies.length()").isEqualTo(5);
    }

    @Test
    @DisplayName("Should handle capacity with no technologies")
    void shouldHandleCapacityWithNoTechnologies() {
        // Given
        CapacityResponse response = new CapacityResponse(
                1L,
                "Empty Tech Capacity",
                "Capacity with no technologies",
                List.of()
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].capacityId").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Empty Tech Capacity")
                .jsonPath("$[0].technologies").isArray()
                .jsonPath("$[0].technologies.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle capacity with special characters in names and descriptions")
    void shouldHandleCapacityWithSpecialCharactersInNamesAndDescriptions() {
        // Given
        CapacityResponse response = new CapacityResponse(
                1L,
                "Capacity with Special Chars: @#$%^&*()",
                "Description with special chars: áéíóú ñ",
                List.of(
                        new TechnologyResponse(1L, "Java 21", "Java 21 LTS"),
                        new TechnologyResponse(2L, "Spring-Boot", "Spring Boot Framework"),
                        new TechnologyResponse(3L, "PostgreSQL 15", "PostgreSQL 15 Database")
                )
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Capacity with Special Chars: @#$%^&*()")
                .jsonPath("$[0].description").isEqualTo("Description with special chars: áéíóú ñ")
                .jsonPath("$[0].technologies[0].name").isEqualTo("Java 21")
                .jsonPath("$[0].technologies[1].name").isEqualTo("Spring-Boot")
                .jsonPath("$[0].technologies[2].name").isEqualTo("PostgreSQL 15");
    }

    @Test
    @DisplayName("Should handle very long capacity names and descriptions")
    void shouldHandleVeryLongCapacityNamesAndDescriptions() {
        // Given
        String longName = "A".repeat(100);
        String longDescription = "B".repeat(500);
        
        CapacityResponse response = new CapacityResponse(
                1L,
                longName,
                longDescription,
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].name").isEqualTo(longName)
                .jsonPath("$[0].description").isEqualTo(longDescription);
    }

    @Test
    @DisplayName("Should handle concurrent GET requests")
    void shouldHandleConcurrentGetRequests() {
        // Given
        CapacityResponse response1 = new CapacityResponse(
                1L,
                "Concurrent Test 1",
                "First concurrent request",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        CapacityResponse response2 = new CapacityResponse(
                2L,
                "Concurrent Test 2",
                "Second concurrent request",
                List.of(new TechnologyResponse(2L, "Python", "Python Programming Language"))
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response1, response2));

        // When & Then - Both requests should be handled
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);

        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle GET request with different HTTP headers")
    void shouldHandleGetRequestWithDifferentHttpHeaders() {
        // Given
        CapacityResponse response = new CapacityResponse(
                1L,
                "Headers Test",
                "Testing different HTTP headers",
                List.of(new TechnologyResponse(1L, "Java", "Java Programming Language"))
        );

        when(getCapacityUseCase.execute()).thenReturn(Flux.just(response));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .header("X-Custom-Header", "CustomValue")
                .header("User-Agent", "TestAgent/1.0")
                .header("Accept", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Headers Test");
    }

    @Test
    @DisplayName("Should handle business exception when retrieving capacities")
    void shouldHandleBusinessExceptionWhenRetrievingCapacities() {
        // Given
        when(getCapacityUseCase.execute()).thenReturn(Flux.error(new BussinessException("No capacities found")));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("BUSINESS_ERROR")
                .jsonPath("$.message").isEqualTo("No capacities found");
    }

    @Test
    @DisplayName("Should handle domain exception when retrieving capacities")
    void shouldHandleDomainExceptionWhenRetrievingCapacities() {
        // Given
        when(getCapacityUseCase.execute()).thenReturn(Flux.error(new RuntimeException("Database connection failed")));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("INTERNAL_ERROR")
                .jsonPath("$.message").isEqualTo("An unexpected error occurred");
    }

    @Test
    @DisplayName("Should handle generic exception when retrieving capacities")
    void shouldHandleGenericExceptionWhenRetrievingCapacities() {
        // Given
        when(getCapacityUseCase.execute()).thenReturn(Flux.error(new IllegalStateException("Unexpected error")));

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("INTERNAL_ERROR")
                .jsonPath("$.message").isEqualTo("An unexpected error occurred");
    }

    @Test
    @DisplayName("Should handle empty response from use case")
    void shouldHandleEmptyResponseFromUseCase() {
        // Given
        when(getCapacityUseCase.execute()).thenReturn(Flux.empty());

        // When & Then
        webTestClient
                .get()
                .uri("/v1/api/capacity")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0);
    }
}
