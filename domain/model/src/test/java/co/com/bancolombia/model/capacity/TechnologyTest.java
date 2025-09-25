package co.com.bancolombia.model.capacity;

import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Technology Domain Tests")
class TechnologyTest {

    @Test
    @DisplayName("Should create technology with all parameters successfully")
    void shouldCreateTechnologyWithAllParametersSuccessfully() {
        // Given
        Long id = 1L;
        String name = "Java";
        String description = "Java programming language";

        // When
        Technology technology = new Technology(id, name, description);

        // Then
        assertNotNull(technology);
        assertEquals(new Id(id).getValue(), technology.getId().getValue());
        assertEquals(new Name(name).getValue(), technology.getName().getValue());
        assertEquals(new Description(description).getValue(), technology.getDescription().getValue());
    }

    @Test
    @DisplayName("Should create technology without id successfully")
    void shouldCreateTechnologyWithoutIdSuccessfully() {
        // Given
        String name = "Spring";
        String description = "Spring Framework";

        // When
        Technology technology = new Technology(name, description);

        // Then
        assertNotNull(technology);
        assertNull(technology.getId());
        assertEquals(new Name(name).getValue(), technology.getName().getValue());
        assertEquals(new Description(description).getValue(), technology.getDescription().getValue());
    }

    @Test
    @DisplayName("Should set and get id correctly")
    void shouldSetAndGetIdCorrectly() {
        // Given
        Technology technology = new Technology("Test", "Test description");
        Id newId = new Id(5L);

        // When
        technology.setId(newId);

        // Then
        assertEquals(newId.getValue(), technology.getId().getValue());
    }

    @Test
    @DisplayName("Should set and get name correctly")
    void shouldSetAndGetNameCorrectly() {
        // Given
        Technology technology = new Technology(1L, "Test", "Test description");
        Name newName = new Name("Updated Technology");

        // When
        technology.setName(newName);

        // Then
        assertEquals(newName.getValue(), technology.getName().getValue());
    }

    @Test
    @DisplayName("Should set and get description correctly")
    void shouldSetAndGetDescriptionCorrectly() {
        // Given
        Technology technology = new Technology(1L, "Test", "Test description");
        Description newDescription = new Description("Updated Description");

        // When
        technology.setDescription(newDescription);

        // Then
        assertEquals(newDescription.getValue(), technology.getDescription().getValue());
    }

    @Test
    @DisplayName("Should handle technology with zero id")
    void shouldHandleTechnologyWithZeroId() {
        // Given
        Long id = 0L;
        String name = "Test Technology";
        String description = "Test description";

        // When
        Technology technology = new Technology(id, name, description);

        // Then
        assertNotNull(technology);
        assertEquals(new Id(id).getValue(), technology.getId().getValue());
        assertEquals(new Name(name).getValue(), technology.getName().getValue());
        assertEquals(new Description(description).getValue(), technology.getDescription().getValue());
    }

    @Test
    @DisplayName("Should handle technology with negative id")
    void shouldHandleTechnologyWithNegativeId() {
        // Given
        Long id = -1L;
        String name = "Test Technology";
        String description = "Test description";

        // When
        Technology technology = new Technology(id, name, description);

        // Then
        assertNotNull(technology);
        assertEquals(new Id(id).getValue(), technology.getId().getValue());
        assertEquals(new Name(name).getValue(), technology.getName().getValue());
        assertEquals(new Description(description).getValue(), technology.getDescription().getValue());
    }

    @Test
    @DisplayName("Should handle technology with maximum name length")
    void shouldHandleTechnologyWithMaximumNameLength() {
        // Given
        Long id = 1L;
        String name = "A".repeat(50); // Maximum length for name
        String description = "Test description";

        // When
        Technology technology = new Technology(id, name, description);

        // Then
        assertNotNull(technology);
        assertEquals(new Id(id).getValue(), technology.getId().getValue());
        assertEquals(new Name(name).getValue(), technology.getName().getValue());
        assertEquals(new Description(description).getValue(), technology.getDescription().getValue());
    }

    @Test
    @DisplayName("Should handle technology with maximum description length")
    void shouldHandleTechnologyWithMaximumDescriptionLength() {
        // Given
        Long id = 1L;
        String name = "Test Technology";
        String description = "A".repeat(90); // Maximum length for description

        // When
        Technology technology = new Technology(id, name, description);

        // Then
        assertNotNull(technology);
        assertEquals(new Id(id).getValue(), technology.getId().getValue());
        assertEquals(new Name(name).getValue(), technology.getName().getValue());
        assertEquals(new Description(description).getValue(), technology.getDescription().getValue());
    }
}
