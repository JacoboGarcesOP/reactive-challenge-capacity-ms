package co.com.bancolombia.model.capacity.values;

import co.com.bancolombia.model.capacity.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Name Value Object Tests")
class NameTest {

    @Test
    @DisplayName("Should create name with valid value successfully")
    void shouldCreateNameWithValidValueSuccessfully() {
        // Given
        String value = "Java";

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(value, name.getValue());
    }

    @Test
    @DisplayName("Should create name with maximum length successfully")
    void shouldCreateNameWithMaximumLengthSuccessfully() {
        // Given
        String value = "A".repeat(50); // Maximum length

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(value, name.getValue());
    }

    @Test
    @DisplayName("Should trim whitespace from name")
    void shouldTrimWhitespaceFromName() {
        // Given
        String value = "  Java  ";
        String expectedValue = "Java";

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(expectedValue, name.getValue());
    }

    @Test
    @DisplayName("Should trim whitespace from name with maximum length")
    void shouldTrimWhitespaceFromNameWithMaximumLength() {
        // Given
        String value = " " + "A".repeat(50) + " ";
        String expectedValue = "A".repeat(50);

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(expectedValue, name.getValue());
    }

    @Test
    @DisplayName("Should throw domain exception when name is null")
    void shouldThrowDomainExceptionWhenNameIsNull() {
        // Given
        String value = null;

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Name(value);
        });

        assertEquals("The name cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when name is empty")
    void shouldThrowDomainExceptionWhenNameIsEmpty() {
        // Given
        String value = "";

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Name(value);
        });

        assertEquals("The name cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when name is only whitespace")
    void shouldThrowDomainExceptionWhenNameIsOnlyWhitespace() {
        // Given
        String value = "   ";

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Name(value);
        });

        assertEquals("The name cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when name exceeds maximum length")
    void shouldThrowDomainExceptionWhenNameExceedsMaximumLength() {
        // Given
        String value = "A".repeat(51); // Exceeds maximum length

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Name(value);
        });

        assertEquals("The name cannot be greater than 50.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when name with whitespace exceeds maximum length")
    void shouldThrowDomainExceptionWhenNameWithWhitespaceExceedsMaximumLength() {
        // Given
        String value = " " + "A".repeat(51) + " "; // 52 characters total, 50 after trim

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Name(value);
        });

        assertEquals("The name cannot be greater than 50.", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle single character name")
    void shouldHandleSingleCharacterName() {
        // Given
        String value = "A";

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(value, name.getValue());
    }

    @Test
    @DisplayName("Should handle name with special characters")
    void shouldHandleNameWithSpecialCharacters() {
        // Given
        String value = "Java-Spring_Boot";

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(value, name.getValue());
    }

    @Test
    @DisplayName("Should handle name with numbers")
    void shouldHandleNameWithNumbers() {
        // Given
        String value = "Java8";

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(value, name.getValue());
    }

    @Test
    @DisplayName("Should handle name with mixed case")
    void shouldHandleNameWithMixedCase() {
        // Given
        String value = "JavaSpringBoot";

        // When
        Name name = new Name(value);

        // Then
        assertNotNull(name);
        assertEquals(value, name.getValue());
    }

    @Test
    @DisplayName("Should return same value when getValue is called multiple times")
    void shouldReturnSameValueWhenGetValueIsCalledMultipleTimes() {
        // Given
        String value = "Java";
        Name name = new Name(value);

        // When
        String firstCall = name.getValue();
        String secondCall = name.getValue();

        // Then
        assertEquals(value, firstCall);
        assertEquals(value, secondCall);
        assertSame(firstCall, secondCall);
    }
}
