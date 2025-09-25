package co.com.bancolombia.model.capacity.exception;

public class DomainException extends RuntimeException {

  public DomainException(String message) {
    super(message);
  }

  public DomainException(String message, Throwable cause) {
    super(message, cause);
  }

  public DomainException(Throwable cause) {
    super(cause);
  }
}