package co.com.bancolombia.usecase.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TechnologyResponse Tests")
class TechnologyResponseTest {

    @Test
    @DisplayName("Should create TechnologyResponse with all parameters successfully")
    void shouldCreateTechnologyResponseWithAllParametersSuccessfully() {
        // Given
        Long technologyId = 1L;
        String name = "Java";
        String description = "Java programming language";

        // When
        TechnologyResponse response = new TechnologyResponse(technologyId, name, description);

        // Then
        assertNotNull(response);
        assertEquals(technologyId, response.getTechnologyId());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
    }

    @Test
    @DisplayName("Should create TechnologyResponse with null values")
    void shouldCreateTechnologyResponseWithNullValues() {
        // Given
        Long technologyId = null;
        String name = null;
        String description = null;

        // When
        TechnologyResponse response = new TechnologyResponse(technologyId, name, description);

        // Then
        assertNotNull(response);
        assertNull(response.getTechnologyId());
        assertNull(response.getName());
        assertNull(response.getDescription());
    }

    @Test
    @DisplayName("Should create TechnologyResponse with empty strings")
    void shouldCreateTechnologyResponseWithEmptyStrings() {
        // Given
        Long technologyId = 1L;
        String name = "";
        String description = "";

        // When
        TechnologyResponse response = new TechnologyResponse(technologyId, name, description);

        // Then
        assertNotNull(response);
        assertEquals(technologyId, response.getTechnologyId());
        assertEquals("", response.getName());
        assertEquals("", response.getDescription());
    }
}
