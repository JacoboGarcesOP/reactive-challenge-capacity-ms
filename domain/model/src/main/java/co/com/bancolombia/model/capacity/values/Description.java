package co.com.bancolombia.model.capacity.values;

import co.com.bancolombia.model.capacity.exception.DomainException;

public class Description {

  private static final String NULL_DESCRIPTION_ERROR_MESSAGE = "The description cannot be null.";
  private static final String MAX_LENGTH_ERROR_MESSAGE = "The description cannot be greater than 90.";
  private static final int MAX_DESCRIPTION_LENGTH = 90;

  private final String value;

  public Description(final String value) {

    if (value == null || value.trim().isEmpty()) {
      throw new DomainException(NULL_DESCRIPTION_ERROR_MESSAGE);
    }

    if (value.trim().length() > MAX_DESCRIPTION_LENGTH) {
      throw new DomainException(MAX_LENGTH_ERROR_MESSAGE);
    }

    this.value = value.trim();
  }

  public String getValue() {
    return value;
  }
}