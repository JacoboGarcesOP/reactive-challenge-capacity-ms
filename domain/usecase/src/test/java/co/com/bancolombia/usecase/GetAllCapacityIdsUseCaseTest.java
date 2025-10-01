package co.com.bancolombia.usecase;

import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.gateway.CapacityGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GetAllCapacityIdsUseCaseTest {

  @Mock
  private CapacityGateway capacityGateway;

  private GetAllCapacityIdsUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase = new GetAllCapacityIdsUseCase(capacityGateway);
  }

  @Test
  @DisplayName("Should return all capacity IDs from gateway")
  void shouldReturnAllCapacityIds() {
    // Given
    Capacity cap1 = new Capacity(1L, "A", "Desc");
    Capacity cap2 = new Capacity(2L, "B", "Desc");
    Capacity cap3 = new Capacity(3L, "C", "Desc");
    when(capacityGateway.findAll()).thenReturn(Flux.just(cap1, cap2, cap3));

    // When
    List<Long> result = useCase.execute().collectList().block();

    // Then
    assertEquals(List.of(1L, 2L, 3L), result);
  }
}


