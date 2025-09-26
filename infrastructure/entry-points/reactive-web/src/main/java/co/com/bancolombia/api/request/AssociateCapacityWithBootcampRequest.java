package co.com.bancolombia.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Request para asociar una tecnología existente con una capacidad específica")
public class AssociateCapacityWithBootcampRequest {
  
  @NotNull(message = "Capacity id is required")
  @Positive(message = "The capacity id should be positive")
  @Schema(
    description = "ID de la capacidad a la cual se asociará el bootcamp",
    example = "1",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minimum = "1"
  )
  private Long capacityId;

  @NotNull(message = "Bootcamp id is required")
  @Positive(message = "The bootcamp id should be positive")
  @Schema(
    description = "ID del bootcamp al cual se asociará la capacidad",
    example = "1",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minimum = "1"
  )
  private Long bootcampId;
}
