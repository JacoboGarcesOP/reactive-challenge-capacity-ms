package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateCapacityRequest;
import io.swagger.v3.oas.annotations.Operation;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    private final String BASE_URL = "/v1/api";
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return createCapacityRoute(handler)
            .andRoute(GET(BASE_URL + "/capacity"), handler::getAllCapacities);
    }

    @Bean
    @RouterOperation(
        path = "/v1/api/capacity",
        produces = { MediaType.APPLICATION_JSON_VALUE },
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
        produces = { MediaType.APPLICATION_JSON_VALUE },
        method = RequestMethod.GET,
        beanClass = Handler.class,
        beanMethod = "getAllCapacities",
        operation = @Operation(
            operationId = "getAllCapacities",
            summary = "Obtener todas las capacidades",
            description = "Retorna todas las capacidades con sus tecnologías asociadas. " +
                "Maneja errores de dominio, negocio e internos.",
            tags = {"Capacity Management"},
            responses = {
                @ApiResponse(responseCode = "200", description = "Capacidades obtenidas exitosamente",
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        examples = @ExampleObject(
                            name = "Success Response",
                            summary = "Lista de capacidades",
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
}
