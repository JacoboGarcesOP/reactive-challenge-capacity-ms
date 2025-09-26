package co.com.bancolombia.api;


import co.com.bancolombia.api.request.AssociateCapacityWithBootcampRequest;
import co.com.bancolombia.api.response.ErrorResponse;
import co.com.bancolombia.model.capacity.exception.DomainException;
import co.com.bancolombia.usecase.AssociateCapacityWithBootcampUseCase;
import co.com.bancolombia.usecase.CreateCapacityUseCase;
import co.com.bancolombia.api.request.CreateCapacityRequest;
import co.com.bancolombia.usecase.GetCapacityUseCase;
import co.com.bancolombia.usecase.command.AssociateCapacityWithBootcampCommand;
import co.com.bancolombia.usecase.command.CreateCapacityCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
  private static final String VALIDATION_ERROR_TEXT = "VALIDATION_ERROR";
  private static final String DOMAIN_ERROR_TEXT = "DOMAIN_ERROR";
  private static final String BUSINESS_ERROR_TEXT = "BUSINESS_ERROR";
  private static final String INTERNAL_ERROR_TEXT = "INTERNAL_ERROR";
  private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";

  private final CreateCapacityUseCase createCapacityUseCase;
  private final GetCapacityUseCase getCapacityUseCase;
  private final AssociateCapacityWithBootcampUseCase associateCapacityWithBootcampUseCase;
  private final Validator validator;

  public Mono<ServerResponse> createCapacity(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(CreateCapacityRequest.class)
      .doOnNext(this::validateRequest)
      .map(this::mapToCommand)
      .flatMap(createCapacityUseCase::execute)
      .flatMap(this::buildSuccessResponse)
      .onErrorResume(ConstraintViolationException.class, this::handleValidationException)
      .onErrorResume(DomainException.class, this::handleDomainException)
      .onErrorResume(BussinessException.class, this::handleBusinessException)
      .onErrorResume(Exception.class, this::handleGenericException)
      .doOnError(error -> log.error(GENERIC_ERROR_MESSAGE, error));
  }

  public Mono<ServerResponse> getAllCapacities(ServerRequest serverRequest) {
    int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
    int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);
    String sortBy = serverRequest.queryParam("sortBy").orElse("name");
    String order = serverRequest.queryParam("order").orElse("asc");

    return getCapacityUseCase.execute(page, size, sortBy, order)
      .flatMap(this::buildSuccessResponse)
      .onErrorResume(DomainException.class, this::handleDomainException)
      .onErrorResume(BussinessException.class, this::handleBusinessException)
      .onErrorResume(Exception.class, this::handleGenericException)
      .doOnError(error -> log.error("Error retrieving capacities", error));
  }

  public Mono<ServerResponse> associateTechnologyWithCapacity(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(AssociateCapacityWithBootcampRequest.class)
      .doOnNext(this::validateAssociateRequest)
      .map(this::mapToAssociateCommand)
      .flatMap(associateCapacityWithBootcampUseCase::execute)
      .flatMap(this::buildSuccessResponse)
      .onErrorResume(ConstraintViolationException.class, this::handleValidationException)
      .onErrorResume(DomainException.class, this::handleDomainException)
      .onErrorResume(BussinessException.class, this::handleBusinessException)
      .onErrorResume(Exception.class, this::handleGenericException)
      .doOnError(error -> log.error(GENERIC_ERROR_MESSAGE, error));
  }

  private void validateAssociateRequest(AssociateCapacityWithBootcampRequest request) {
    Set<ConstraintViolation<AssociateCapacityWithBootcampRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private void validateRequest(CreateCapacityRequest request) {
    Set<ConstraintViolation<CreateCapacityRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private AssociateCapacityWithBootcampCommand mapToAssociateCommand(AssociateCapacityWithBootcampRequest request) {
    return new AssociateCapacityWithBootcampCommand(request.getCapacityId(), request.getBootcampId());
  }

  private CreateCapacityCommand mapToCommand(CreateCapacityRequest request) {
    return new CreateCapacityCommand(request.getName(), request.getDescription(), request.getTechnologyNames());
  }

  private Mono<ServerResponse> buildSuccessResponse(Object response) {
    return ServerResponse.ok()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(response);
  }

  private Mono<ServerResponse> handleValidationException(ConstraintViolationException ex) {
    String errorMessage = ex.getConstraintViolations().stream()
      .map(ConstraintViolation::getMessage)
      .collect(Collectors.joining(", "));

    log.warn("Validation error: {}", errorMessage);

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(VALIDATION_ERROR_TEXT, errorMessage));
  }

  private Mono<ServerResponse> handleDomainException(DomainException ex) {
    log.warn("Domain error: {}", ex.getMessage());

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(DOMAIN_ERROR_TEXT, ex.getMessage()));
  }

  private Mono<ServerResponse> handleBusinessException(BussinessException ex) {
    log.warn("Business error: {}", ex.getMessage());

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(BUSINESS_ERROR_TEXT, ex.getMessage()));
  }

  private Mono<ServerResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(INTERNAL_ERROR_TEXT, GENERIC_ERROR_MESSAGE));
  }

  private ErrorResponse createErrorResponse(String error, String message) {
    return new ErrorResponse(error, message);
  }
}