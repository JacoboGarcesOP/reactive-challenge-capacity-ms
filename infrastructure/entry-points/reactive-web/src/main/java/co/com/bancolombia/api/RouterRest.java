package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateCapacityRequest;
import co.com.bancolombia.api.request.AssociateCapacityWithBootcampRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
  private final String BASE_URL = "/v1/api";

  @Bean
  @RouterOperation(
    path = "/v1/api/capacity",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.POST,
    beanClass = Handler.class,
    beanMethod = "createCapacity",
    operation = @Operation(
      operationId = "createCapacity",
      summary = "Crear nueva capacidad",
      description = "Crea una capacidad con sus tecnologías asociadas. " +
        "Valida los datos de entrada y maneja errores de validación, dominio, negocio e internos.",
      tags = {"Capacity Management"},
      requestBody = @RequestBody(
        required = true,
        description = "Datos de la capacidad a crear. Requiere nombre (máx 50) y descripción (máx 90), y lista de tecnologías (3 a 20).",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = CreateCapacityRequest.class),
          examples = @ExampleObject(
            name = "Ejemplo de capacidad",
            summary = "Request para crear capacidad",
            value = "{\n" +
              "  \"name\": \"Payments Squad\",\n" +
              "  \"description\": \"Handles all payment features\",\n" +
              "  \"technologyNames\": [\"Java\", \"Spring Boot\", \"PostgreSQL\"]\n" +
              "}"
          )
        )
      ),
      responses = {
        @ApiResponse(responseCode = "200", description = "Capacidad creada exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Capacidad creada",
              value = "{\n" +
                "  \"capacityId\": 123,\n" +
                "  \"name\": \"Payments Squad\",\n" +
                "  \"description\": \"Handles all payment features\",\n" +
                "  \"technologies\": [\n" +
                "    { \"technologyId\": 10, \"name\": \"Java\", \"description\": \"Java 21 LTS\" }\n" +
                "  ]\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de validación, dominio o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Validation Error",
              summary = "Error de validación",
              value = "{\n" +
                "  \"error\": \"VALIDATION_ERROR\",\n" +
                "  \"message\": \"Capacity name is required, Technology names are required\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno",
              value = "{\n" +
                "  \"error\": \"INTERNAL_ERROR\",\n" +
                "  \"message\": \"An unexpected error occurred\"\n" +
                "}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> createCapacityRoute(Handler handler) {
    return route(POST(BASE_URL + "/capacity"), handler::createCapacity);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/capacity",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.GET,
    beanClass = Handler.class,
    beanMethod = "getAllCapacities",
    operation = @Operation(
      operationId = "getAllCapacities",
      summary = "Obtener capacidades paginadas y ordenadas",
      description = "Retorna capacidades con sus tecnologías asociadas, soportando paginación y ordenamiento. " +
        "Maneja errores de dominio, negocio e internos.",
      tags = {"Capacity Management"},
      parameters = {
        @Parameter(name = "page", description = "Número de página (base 0)", example = "0", schema = @Schema(type = "integer", defaultValue = "0")),
        @Parameter(name = "size", description = "Tamaño de página", example = "10", schema = @Schema(type = "integer", defaultValue = "10")),
        @Parameter(name = "sortBy", description = "Campo por el cual ordenar", example = "name", schema = @Schema(type = "string", allowableValues = {"name", "technologies"}, defaultValue = "name")),
        @Parameter(name = "order", description = "Dirección del ordenamiento", example = "asc", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}, defaultValue = "asc"))
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "Capacidades obtenidas exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Lista de capacidades paginadas y ordenadas",
              value = "{\n" +
                "  \"capacities\": [\n" +
                "    {\n" +
                "      \"capacityId\": 123,\n" +
                "      \"name\": \"Payments Squad\",\n" +
                "      \"description\": \"Handles all payment features\",\n" +
                "      \"technologies\": [\n" +
                "        { \"technologyId\": 10, \"name\": \"Java\", \"description\": \"Java 21 LTS\" },\n" +
                "        { \"technologyId\": 11, \"name\": \"Spring Boot\", \"description\": \"Spring Boot Framework\" }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"capacityId\": 124,\n" +
                "      \"name\": \"User Management\",\n" +
                "      \"description\": \"Handles user operations\",\n" +
                "      \"technologies\": [\n" +
                "        { \"technologyId\": 12, \"name\": \"React\", \"description\": \"React Framework\" }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"filter\": {\n" +
                "    \"page\": 0,\n" +
                "    \"size\": 10,\n" +
                "    \"sortBy\": \"name\",\n" +
                "    \"order\": \"asc\"\n" +
                "  }\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de dominio o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Business Error",
              summary = "Error de negocio",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"No capacities found\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno",
              value = "{\n" +
                "  \"error\": \"INTERNAL_ERROR\",\n" +
                "  \"message\": \"An unexpected error occurred\"\n" +
                "}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> getAllCapacitiesRoute(Handler handler) {
    return route(GET(BASE_URL + "/capacity"), handler::getAllCapacities);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/capacity/ids",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.GET,
    beanClass = Handler.class,
    beanMethod = "getAllCapacityIds",
    operation = @Operation(
      operationId = "getAllCapacityIds",
      summary = "Obtener todos los IDs de capacidades",
      description = "Retorna una lista de IDs de todas las capacidades registradas. Maneja errores de dominio, negocio e internos.",
      tags = {"Capacity Management"},
      responses = {
        @ApiResponse(responseCode = "200", description = "IDs obtenidos exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Lista de IDs de capacidades",
              value = "[1, 2, 3, 4]"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de dominio o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Business Error",
              summary = "Error de negocio",
              value = "{\n  \"error\": \"BUSINESS_ERROR\",\n  \"message\": \"No capacities found\"\n}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno",
              value = "{\n  \"error\": \"INTERNAL_ERROR\",\n  \"message\": \"An unexpected error occurred\"\n}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> getAllCapacityIdsRoute(Handler handler) {
    return route(GET(BASE_URL + "/capacity/ids"), handler::getAllCapacityIds);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/capacity/associate",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.POST,
    beanClass = Handler.class,
    beanMethod = "associateTechnologyWithCapacity",
    operation = @Operation(
      operationId = "associateCapacityWithBootcamp",
      summary = "Asociar capacidad con bootcamp",
      description = "Asocia una capacidad existente con un bootcamp. Valida el request y maneja errores de validación, dominio/negocio e internos.",
      tags = {"Capacity Management"},
      requestBody = @RequestBody(
        required = true,
        description = "IDs de capacidad y bootcamp a asociar",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = AssociateCapacityWithBootcampRequest.class),
          examples = @ExampleObject(
            name = "Ejemplo de asociación",
            summary = "Request para asociar capacidad con bootcamp",
            value = "{\n" +
              "  \"capacityId\": 1,\n" +
              "  \"bootcampId\": 100\n" +
              "}"
          )
        )
      ),
      responses = {
        @ApiResponse(responseCode = "200", description = "Asociación creada exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Capacidad asociada al bootcamp",
              value = "{\n" +
                "  \"capacityId\": 1,\n" +
                "  \"name\": \"Payments Squad\",\n" +
                "  \"description\": \"Handles all payment features\",\n" +
                "  \"technologies\": [\n" +
                "    { \"technologyId\": 10, \"name\": \"Java\", \"description\": \"Java 21 LTS\" }\n" +
                "  ],\n" +
                "  \"bootcampId\": 100\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de validación o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Validation/Business Error",
              summary = "Error de validación o negocio",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"The capacity is already associated with this bootcamp.\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno",
              value = "{\n" +
                "  \"error\": \"INTERNAL_ERROR\",\n" +
                "  \"message\": \"An unexpected error occurred\"\n" +
                "}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> associateCapacityWithBootcampRoute(Handler handler) {
    return route(POST(BASE_URL + "/capacity/associate"), handler::associateTechnologyWithCapacity);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/capacity/bootcamp/{bootcampId}",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.GET,
    beanClass = Handler.class,
    beanMethod = "getCapacitiesByBootcamp",
    operation = @Operation(
      operationId = "getCapacitiesByBootcamp",
      summary = "Obtener capacidades por bootcamp",
      description = "Retorna todas las capacidades asociadas a un bootcamp específico con sus tecnologías. " +
        "Maneja errores de dominio, negocio e internos.",
      tags = {"Capacity Management"},
      parameters = {
        @Parameter(
          name = "bootcampId", 
          description = "ID del bootcamp para obtener sus capacidades", 
          example = "100", 
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "integer", format = "int64")
        )
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "Capacidades obtenidas exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Lista de capacidades del bootcamp",
              value = "[\n" +
                "  {\n" +
                "    \"capacityId\": 123,\n" +
                "    \"name\": \"Payments Squad\",\n" +
                "    \"description\": \"Handles all payment features\",\n" +
                "    \"technologies\": [\n" +
                "      { \"technologyId\": 10, \"name\": \"Java\", \"description\": \"Java 21 LTS\" },\n" +
                "      { \"technologyId\": 11, \"name\": \"Spring Boot\", \"description\": \"Spring Boot Framework\" }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"capacityId\": 124,\n" +
                "    \"name\": \"User Management\",\n" +
                "    \"description\": \"Handles user operations\",\n" +
                "    \"technologies\": [\n" +
                "      { \"technologyId\": 12, \"name\": \"React\", \"description\": \"React Framework\" }\n" +
                "    ]\n" +
                "  }\n" +
                "]"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de dominio o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Business Error",
              summary = "Error de negocio",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"Bootcamp not found\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno",
              value = "{\n" +
                "  \"error\": \"INTERNAL_ERROR\",\n" +
                "  \"message\": \"An unexpected error occurred\"\n" +
                "}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> getCapacitiesByBootcampRoute(Handler handler) {
    return route(GET(BASE_URL + "/capacity/bootcamp/{bootcampId}"), handler::getCapacitiesByBootcamp);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/capacity/bootcamp/{bootcampId}",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.DELETE,
    beanClass = Handler.class,
    beanMethod = "deleteCapacitiesByBootcamp",
    operation = @Operation(
      operationId = "deleteCapacitiesByBootcamp",
      summary = "Eliminar capacidades por bootcamp",
      description = "Elimina todas las capacidades asociadas a un bootcamp específico. " +
        "Si una capacidad solo está asociada al bootcamp dado, se elimina completamente. " +
        "Si está asociada a otros bootcamps, solo se elimina la relación. " +
        "Maneja errores de dominio, negocio e internos.",
      tags = {"Capacity Management"},
      parameters = {
        @Parameter(
          name = "bootcampId", 
          description = "ID del bootcamp para eliminar sus capacidades", 
          example = "100", 
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "integer", format = "int64")
        )
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "Capacidades eliminadas exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Lista de IDs de capacidades eliminadas",
              value = "[1, 2, 3]"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de dominio o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Business Error",
              summary = "Error de negocio",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"Bootcamp has not been found. Bootcamp id: 100\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno",
              value = "{\n" +
                "  \"error\": \"INTERNAL_ERROR\",\n" +
                "  \"message\": \"An unexpected error occurred\"\n" +
                "}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> deleteCapacitiesByBootcampRoute(Handler handler) {
    return route(DELETE(BASE_URL + "/capacity/bootcamp/{bootcampId}"), handler::deleteCapacitiesByBootcamp);
  }
}
