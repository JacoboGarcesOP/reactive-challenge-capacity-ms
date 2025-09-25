package co.com.bancolombia.api.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "CreateCapacityRequest", description = "Request payload to create a capacity")
public class CreateCapacityRequest {
  @NotNull(message = "Capacity name is required")
  @NotBlank(message = "Capacity name cannot be empty")
  @Schema(description = "Capacity name", example = "Payments Squad")
  private String name;
  @NotNull(message = "Capacity name is required")
  @NotBlank(message = "Capacity name cannot be empty")
  @Schema(description = "Capacity description", example = "Handles all payment related features")
  private String description;
  @NotNull(message = "Technology names are required")
  @ArraySchema(arraySchema = @Schema(description = "List of technology names to associate"),
    minItems = 3, maxItems = 20,
    schema = @Schema(example = "Java, Spring, PostgreSQL"))
  private List<String> technologyNames;
}
