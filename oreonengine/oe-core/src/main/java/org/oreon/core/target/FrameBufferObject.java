package org.oreon.core.target;

import lombok.Getter;

@Getter
public abstract class FrameBufferObject {

  private final int height;
  private final int width;
  private final int depthAttachmentCount;
  private int colorAttachmentCount;

  protected FrameBufferObject(final int width, final int height, final int depthAttachmentCount) {
    this.width = width;
    this.height = height;
    this.depthAttachmentCount = depthAttachmentCount;
  }

  protected void setColorAttachmentCount(final int colorAttachmentCount) {
    this.colorAttachmentCount = colorAttachmentCount;
  }
}
