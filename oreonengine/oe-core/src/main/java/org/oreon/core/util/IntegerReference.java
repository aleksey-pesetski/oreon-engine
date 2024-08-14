package org.oreon.core.util;

import lombok.Data;

@Data
public class IntegerReference {

  private int value;

  public IntegerReference() {
  }

  public IntegerReference(int value) {
    setValue(value);
  }
}
