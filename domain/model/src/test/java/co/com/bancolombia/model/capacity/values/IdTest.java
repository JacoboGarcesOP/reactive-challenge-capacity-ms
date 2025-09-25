package co.com.bancolombia.model.capacity.values;

import co.com.bancolombia.model.capacity.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Id Value Object Tests")
class IdTest {

    @Test
    @DisplayName("Should create id with valid value successfully")
    void shouldCreateIdWithValidValueSuccessfully() {
        // Given
        Long value = 1L;

        // When
        Id id = new Id(value);

        // Then
        assertNotNull(id);
        assertEquals(value, id.getValue());
    }

    @Test
    @DisplayName("Should create id with zero value successfully")
    void shouldCreateIdWithZeroValueSuccessfully() {
        // Given
        Long value = 0L;

        // When
        Id id = new Id(value);

        // Then
        assertNotNull(id);
        assertEquals(value, id.getValue());
    }

    @Test
    @DisplayName("Should create id with negative value successfully")
    void shouldCreateIdWithNegativeValueSuccessfully() {
        // Given
        Long value = -1L;

        // When
        Id id = new Id(value);

        // Then
        assertNotNull(id);
        assertEquals(value, id.getValue());
    }

    @Test
    @DisplayName("Should create id with maximum long value successfully")
    void shouldCreateIdWithMaximumLongValueSuccessfully() {
        // Given
        Long value = Long.MAX_VALUE;

        // When
        Id id = new Id(value);

        // Then
        assertNotNull(id);
        assertEquals(value, id.getValue());
    }

    @Test
    @DisplayName("Should create id with minimum long value successfully")
    void shouldCreateIdWithMinimumLongValueSuccessfully() {
        // Given
        Long value = Long.MIN_VALUE;

        // When
        Id id = new Id(value);

        // Then
        assertNotNull(id);
        assertEquals(value, id.getValue());
    }

    @Test
    @DisplayName("Should throw domain exception when id is null")
    void shouldThrowDomainExceptionWhenIdIsNull() {
        // Given
        Long value = null;

        // When & Then
        DomainException exception = assertThrows(DomainException.class, () -> {
            new Id(value);
        });

        assertEquals("The id cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should return same value when getValue is called multiple times")
    void shouldReturnSameValueWhenGetValueIsCalledMultipleTimes() {
        // Given
        Long value = 123L;
        Id id = new Id(value);

        // When
        Long firstCall = id.getValue();
        Long secondCall = id.getValue();

        // Then
        assertEquals(value, firstCall);
        assertEquals(value, secondCall);
        assertSame(firstCall, secondCall);
    }

    @Test
    @DisplayName("Should handle large positive id values")
    void shouldHandleLargePositiveIdValues() {
        // Given
        Long value = 999999999L;

        // When
        Id id = new Id(value);

        // Then
        assertNotNull(id);
        assertEquals(value, id.getValue());
    }

    @Test
    @DisplayName("Should handle large negative id values")
    void shouldHandleLargeNegativeIdValues() {
        // Given
        Long value = -999999999L;

        // When
        Id id = new Id(value);

        // Then
        assertNotNull(id);
        assertEquals(value, id.getValue());
    }
}
