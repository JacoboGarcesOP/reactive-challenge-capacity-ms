package co.com.bancolombia.model.capacity;

import co.com.bancolombia.model.capacity.values.Description;
import co.com.bancolombia.model.capacity.values.Id;
import co.com.bancolombia.model.capacity.values.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Capacity Domain Tests")
class CapacityTest {

    private Technology technology1;
    private Technology technology2;
    private List<Technology> technologies;

    @BeforeEach
    void setUp() {
        technology1 = new Technology(1L, "Java", "Java programming language");
        technology2 = new Technology(2L, "Spring", "Spring Framework");
        technologies = Arrays.asList(technology1, technology2);
    }

    @Test
    @DisplayName("Should create capacity with all parameters successfully")
    void shouldCreateCapacityWithAllParametersSuccessfully() {
        // Given
        Long id = 1L;
        String name = "Backend Development";
        String description = "Backend development capacity";
        List<Technology> technologies = Arrays.asList(technology1, technology2);

        // When
        Capacity capacity = new Capacity(id, name, description, technologies);

        // Then
        assertNotNull(capacity);
        assertEquals(new Id(id).getValue(), capacity.getId().getValue());
        assertEquals(new Name(name).getValue(), capacity.getName().getValue());
        assertEquals(new Description(description).getValue(), capacity.getDescription().getValue());
        assertEquals(technologies.size(), capacity.getTechnologies().size());
    }

    @Test
    @DisplayName("Should create capacity without id successfully")
    void shouldCreateCapacityWithoutIdSuccessfully() {
        // Given
        String name = "Frontend Development";
        String description = "Frontend development capacity";
        List<Technology> technologies = Arrays.asList(technology1);

        // When
        Capacity capacity = new Capacity(name, description, technologies);

        // Then
        assertNotNull(capacity);
        assertNull(capacity.getId());
        assertEquals(new Name(name).getValue(), capacity.getName().getValue());
        assertEquals(new Description(description).getValue(), capacity.getDescription().getValue());
        assertEquals(technologies, capacity.getTechnologies());
    }

    @Test
    @DisplayName("Should throw exception when technologies list is null in constructor with id")
    void shouldThrowExceptionWhenTechnologiesListIsNullInConstructorWithId() {
        // Given
        Long id = 1L;
        String name = "Backend Development";
        String description = "Backend development capacity";

        // When & Then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new Capacity(id, name, description, null);
        });

        assertEquals("The technologies list cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow null technologies in constructor without id")
    void shouldAllowNullTechnologiesInConstructorWithoutId() {
        // Given
        String name = "Frontend Development";
        String description = "Frontend development capacity";

        // When
        Capacity capacity = new Capacity(name, description, null);

        // Then
        assertNotNull(capacity);
        assertNull(capacity.getId());
        assertEquals(new Name(name).getValue(), capacity.getName().getValue());
        assertEquals(new Description(description).getValue(), capacity.getDescription().getValue());
        assertNull(capacity.getTechnologies());
    }

    @Test
    @DisplayName("Should set and get id correctly")
    void shouldSetAndGetIdCorrectly() {
        // Given
        Capacity capacity = new Capacity("Test", "Test description", technologies);
        Id newId = new Id(5L);

        // When
        capacity.setId(newId);

        // Then
        assertEquals(newId.getValue(), capacity.getId().getValue());
    }

    @Test
    @DisplayName("Should set and get name correctly")
    void shouldSetAndGetNameCorrectly() {
        // Given
        Capacity capacity = new Capacity("Test", "Test description", technologies);
        Name newName = new Name("Updated Name");

        // When
        capacity.setName(newName);

        // Then
        assertEquals(newName.getValue(), capacity.getName().getValue());
    }

    @Test
    @DisplayName("Should set and get description correctly")
    void shouldSetAndGetDescriptionCorrectly() {
        // Given
        Capacity capacity = new Capacity("Test", "Test description", technologies);
        Description newDescription = new Description("Updated Description");

        // When
        capacity.setDescription(newDescription);

        // Then
        assertEquals(newDescription.getValue(), capacity.getDescription().getValue());
    }

    @Test
    @DisplayName("Should set and get technologies correctly")
    void shouldSetAndGetTechnologiesCorrectly() {
        // Given
        Capacity capacity = new Capacity("Test", "Test description", technologies);
        List<Technology> newTechnologies = Arrays.asList(technology1);

        // When
        capacity.setTechnologies(newTechnologies);

        // Then
        assertEquals(newTechnologies, capacity.getTechnologies());
    }

    @Test
    @DisplayName("Should handle empty technologies list")
    void shouldHandleEmptyTechnologiesList() {
        // Given
        Long id = 1L;
        String name = "Backend Development";
        String description = "Backend development capacity";
        List<Technology> emptyTechnologies = Collections.emptyList();

        // When
        Capacity capacity = new Capacity(id, name, description, emptyTechnologies);

        // Then
        assertNotNull(capacity);
        assertEquals(emptyTechnologies, capacity.getTechnologies());
        assertTrue(capacity.getTechnologies().isEmpty());
    }

    @Test
    @DisplayName("Should handle single technology in list")
    void shouldHandleSingleTechnologyInList() {
        // Given
        Long id = 1L;
        String name = "Backend Development";
        String description = "Backend development capacity";
        List<Technology> singleTechnology = Collections.singletonList(technology1);

        // When
        Capacity capacity = new Capacity(id, name, description, singleTechnology);

        // Then
        assertNotNull(capacity);
        assertEquals(singleTechnology, capacity.getTechnologies());
        assertEquals(1, capacity.getTechnologies().size());
        assertEquals(technology1, capacity.getTechnologies().get(0));
    }
}
