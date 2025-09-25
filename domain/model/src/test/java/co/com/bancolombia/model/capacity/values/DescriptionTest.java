package co.com.bancolombia.model.capacity.values;

import co.com.bancolombia.model.capacity.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Description Value Object Tests")
class DescriptionTest {

    @Test
    @DisplayName("Should create description with valid value successfully")
    void shouldCreateDescriptionWithValidValueSuccessfully() {
        // Given
        String value = "Java programming language";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should create description with maximum length successfully")
    void shouldCreateDescriptionWithMaximumLengthSuccessfully() {
        // Given
        String value = "A".repeat(90); // Maximum length

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should trim whitespace from description")
    void shouldTrimWhitespaceFromDescription() {
        // Given
        String value = "  Java programming language  ";
        String expectedValue = "Java programming language";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(expectedValue, description.getValue());
    }

    @Test
    @DisplayName("Should trim whitespace from description with maximum length")
    void shouldTrimWhitespaceFromDescriptionWithMaximumLength() {
        // Given
        String value = " " + "A".repeat(90) + " ";
        String expectedValue = "A".repeat(90);

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(expectedValue, description.getValue());
    }

    @Test
    @DisplayName("Should throw domain exception when description is null")
    void shouldThrowDomainExceptionWhenDescriptionIsNull() {
        // Given
        String value = null;

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Description(value);
        });

        assertEquals("The description cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when description is empty")
    void shouldThrowDomainExceptionWhenDescriptionIsEmpty() {
        // Given
        String value = "";

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Description(value);
        });

        assertEquals("The description cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when description is only whitespace")
    void shouldThrowDomainExceptionWhenDescriptionIsOnlyWhitespace() {
        // Given
        String value = "   ";

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Description(value);
        });

        assertEquals("The description cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when description exceeds maximum length")
    void shouldThrowDomainExceptionWhenDescriptionExceedsMaximumLength() {
        // Given
        String value = "A".repeat(91); // Exceeds maximum length

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Description(value);
        });

        assertEquals("The description cannot be greater than 90.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw domain exception when description with whitespace exceeds maximum length")
    void shouldThrowDomainExceptionWhenDescriptionWithWhitespaceExceedsMaximumLength() {
        // Given
        String value = " " + "A".repeat(91) + " "; // 92 characters total, 90 after trim

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Description(value);
        });

        assertEquals("The description cannot be greater than 90.", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle single character description")
    void shouldHandleSingleCharacterDescription() {
        // Given
        String value = "A";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should handle description with special characters")
    void shouldHandleDescriptionWithSpecialCharacters() {
        // Given
        String value = "Java-Spring_Boot Framework (v2.0+)";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should handle description with numbers")
    void shouldHandleDescriptionWithNumbers() {
        // Given
        String value = "Java 8 programming language";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should handle description with mixed case")
    void shouldHandleDescriptionWithMixedCase() {
        // Given
        String value = "Java Spring Boot Framework";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should handle description with punctuation")
    void shouldHandleDescriptionWithPunctuation() {
        // Given
        String value = "Java programming language, including Spring Boot framework.";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should handle description with newlines and tabs")
    void shouldHandleDescriptionWithNewlinesAndTabs() {
        // Given
        String value = "Java programming language\nwith Spring Boot\tframework";

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
    }

    @Test
    @DisplayName("Should return same value when getValue is called multiple times")
    void shouldReturnSameValueWhenGetValueIsCalledMultipleTimes() {
        // Given
        String value = "Java programming language";
        Description description = new Description(value);

        // When
        String firstCall = description.getValue();
        String secondCall = description.getValue();

        // Then
        assertEquals(value, firstCall);
        assertEquals(value, secondCall);
        assertSame(firstCall, secondCall);
    }

    @Test
    @DisplayName("Should handle description with exactly 90 characters")
    void shouldHandleDescriptionWithExactly90Characters() {
        // Given
        String value = "A".repeat(90);

        // When
        Description description = new Description(value);

        // Then
        assertNotNull(description);
        assertEquals(value, description.getValue());
        assertEquals(90, description.getValue().length());
    }
}
