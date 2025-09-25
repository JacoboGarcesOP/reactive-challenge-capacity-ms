package co.com.bancolombia.usecase.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CapacityResponse Tests")
class CapacityResponseTest {

    @Test
    @DisplayName("Should create CapacityResponse with all parameters successfully")
    void shouldCreateCapacityResponseWithAllParametersSuccessfully() {
        // Given
        Long capacityId = 1L;
        String name = "Backend Development";
        String description = "Backend development capacity";
        TechnologyResponse tech1 = new TechnologyResponse(1L, "Java", "Java programming language");
        TechnologyResponse tech2 = new TechnologyResponse(2L, "Spring", "Spring Framework");
        List<TechnologyResponse> technologies = Arrays.asList(tech1, tech2);

        // When
        CapacityResponse response = new CapacityResponse(capacityId, name, description, technologies);

        // Then
        assertNotNull(response);
        assertEquals(capacityId, response.getCapacityId());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(2, response.getTechnologies().size());
        assertEquals(tech1.getName(), response.getTechnologies().get(0).getName());
        assertEquals(tech2.getName(), response.getTechnologies().get(1).getName());
    }

    @Test
    @DisplayName("Should create CapacityResponse with empty technologies list")
    void shouldCreateCapacityResponseWithEmptyTechnologiesList() {
        // Given
        Long capacityId = 1L;
        String name = "Backend Development";
        String description = "Backend development capacity";
        List<TechnologyResponse> technologies = Arrays.asList();

        // When
        CapacityResponse response = new CapacityResponse(capacityId, name, description, technologies);

        // Then
        assertNotNull(response);
        assertEquals(capacityId, response.getCapacityId());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertTrue(response.getTechnologies().isEmpty());
    }

    @Test
    @DisplayName("Should create CapacityResponse with null technologies list")
    void shouldCreateCapacityResponseWithNullTechnologiesList() {
        // Given
        Long capacityId = 1L;
        String name = "Backend Development";
        String description = "Backend development capacity";

        // When
        CapacityResponse response = new CapacityResponse(capacityId, name, description, null);

        // Then
        assertNotNull(response);
        assertEquals(capacityId, response.getCapacityId());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertNull(response.getTechnologies());
    }
}
