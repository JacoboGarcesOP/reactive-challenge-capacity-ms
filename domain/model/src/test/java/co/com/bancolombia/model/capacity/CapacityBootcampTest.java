package co.com.bancolombia.model.capacity;

import co.com.bancolombia.model.capacity.values.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CapacityBootcamp Tests")
class CapacityBootcampTest {

  @Test
  @DisplayName("Should create CapacityBootcamp with provided IDs")
  void shouldCreateCapacityBootcampWithProvidedIds() {
    CapacityBootcamp cb = new CapacityBootcamp(100L, 1L);
    assertThat(cb.getBootcampId()).isNotNull();
    assertThat(cb.getCapacityId()).isNotNull();
    assertThat(cb.getBootcampId().getValue()).isEqualTo(100L);
    assertThat(cb.getCapacityId().getValue()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Should allow updating IDs via setters")
  void shouldAllowUpdatingIdsViaSetters() {
    CapacityBootcamp cb = new CapacityBootcamp(1L, 2L);
    cb.setBootcampId(new Id(200L));
    cb.setCapacityId(new Id(300L));

    assertThat(cb.getBootcampId().getValue()).isEqualTo(200L);
    assertThat(cb.getCapacityId().getValue()).isEqualTo(300L);
  }
}


