package org.oreon.core.target;

public enum Attachment {
  COLOR(0),
  ALPHA(1),
  POSITION(2),
  NORMAL(3),
  SPECULAR_EMISSION_DIFFUSE_SSAO_BLOOM(4),
  LIGHT_SCATTERING(5),
  DEPTH(6);

  private final int order;

  Attachment(int order) {
    this.order = order;
  }

  public int getOrder() {
    return order;
  }
}
