package co.com.bancolombia.usecase.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateCapacityCommand Tests")
class CreateCapacityCommandTest {

    @Test
    @DisplayName("Should create CreateCapacityCommand with all parameters successfully")
    void shouldCreateCreateCapacityCommandWithAllParametersSuccessfully() {
        // Given
        String name = "Backend Development";
        String description = "Backend development capacity";
        List<String> technologyNames = Arrays.asList("Java", "Spring", "PostgreSQL");

        // When
        CreateCapacityCommand command = new CreateCapacityCommand(name, description, technologyNames);

        // Then
        assertNotNull(command);
        assertEquals(name, command.getName());
        assertEquals(description, command.getDescription());
        assertEquals(3, command.getTechnologyNames().size());
        assertTrue(command.getTechnologyNames().contains("Java"));
        assertTrue(command.getTechnologyNames().contains("Spring"));
        assertTrue(command.getTechnologyNames().contains("PostgreSQL"));
    }

    @Test
    @DisplayName("Should create CreateCapacityCommand with null values")
    void shouldCreateCreateCapacityCommandWithNullValues() {
        // Given
        String name = null;
        String description = null;
        List<String> technologyNames = null;

        // When
        CreateCapacityCommand command = new CreateCapacityCommand(name, description, technologyNames);

        // Then
        assertNotNull(command);
        assertNull(command.getName());
        assertNull(command.getDescription());
        assertNull(command.getTechnologyNames());
    }

    @Test
    @DisplayName("Should create CreateCapacityCommand with empty technology names list")
    void shouldCreateCreateCapacityCommandWithEmptyTechnologyNamesList() {
        // Given
        String name = "Backend Development";
        String description = "Backend development capacity";
        List<String> technologyNames = Arrays.asList();

        // When
        CreateCapacityCommand command = new CreateCapacityCommand(name, description, technologyNames);

        // Then
        assertNotNull(command);
        assertEquals(name, command.getName());
        assertEquals(description, command.getDescription());
        assertTrue(command.getTechnologyNames().isEmpty());
    }

    @Test
    @DisplayName("Should create CreateCapacityCommand with empty strings")
    void shouldCreateCreateCapacityCommandWithEmptyStrings() {
        // Given
        String name = "";
        String description = "";
        List<String> technologyNames = Arrays.asList("Java", "Spring");

        // When
        CreateCapacityCommand command = new CreateCapacityCommand(name, description, technologyNames);

        // Then
        assertNotNull(command);
        assertEquals("", command.getName());
        assertEquals("", command.getDescription());
        assertEquals(2, command.getTechnologyNames().size());
    }
}
