package co.com.bancolombia.model.capacity.values;

import co.com.bancolombia.model.capacity.exception.DomainException;

public class Id {

  private static final String NULL_ID_ERROR_MESSAGE = "The id cannot be null.";

  private final Long value;

  public Id(final Long value) {

    if (value == null) {
      throw new DomainException(NULL_ID_ERROR_MESSAGE);
    }

    this.value = value;
  }

  public Long getValue() {
    return value;
  }
}