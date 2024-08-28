package org.oreon.core.gl.framebuffer;

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL30;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.target.Attachment;
import org.oreon.core.target.FrameBufferObject;
import org.oreon.core.util.BufferUtil;

@Getter
public abstract class GLFrameBufferObject extends FrameBufferObject {

  private final GLFramebuffer frameBuffer;
  private final Map<Attachment, GLTexture> attachments;

  protected GLFrameBufferObject(final int width, final int height, final int depthAttachmentCount,
      final int sample) {
    super(width, height, depthAttachmentCount);
    this.frameBuffer = new GLFramebuffer();
    this.attachments = configureAttachments(sample);

    var attachmentTypes = attachments.keySet().stream()
        .sorted(Comparator.comparingInt(Attachment::getOrder))
        .toList();

    int elementCount = (int) attachmentTypes.stream().filter(type -> Attachment.DEPTH != type).count();
    if (elementCount > 1) {
      final IntBuffer drawBuffers = BufferUtil.createIntBuffer(elementCount);
      IntStream.range(0, elementCount)
          .forEach(index -> {
            try {
              final Field declaredField = GL30.class.getDeclaredField("GL_COLOR_ATTACHMENT%s".formatted(index));
              getLog().debug("Draw buffer {} attached to {}.", index, declaredField.getName());
              drawBuffers.put(declaredField.getInt(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
              getLog().error("Can't get GL_COLOR_ATTACHMENT{}.", index, e);
              System.exit(-1);
            }
          });
      drawBuffers.flip();

      frameBuffer.bind();

      IntStream.range(0, attachmentTypes.size())
          .forEach(index -> {
            final var attachment = attachmentTypes.get(index);
            if (attachment != Attachment.DEPTH) {
              if (sample > 0) {
                getLog().debug("Create color texture attachment {} of sample {}.", index, sample);
                frameBuffer.createColorTextureAttachment(attachments.get(attachment).getHandle(), index, (sample > 1));
              } else {
                getLog().debug("Create color texture attachment {}.", index);
                frameBuffer.createColorTextureAttachment(attachments.get(attachment).getHandle(), index);
              }
            } else {
              if (sample > 0) {
                getLog().debug("Create depth texture attachment of sample {}.", sample);
                frameBuffer.createDepthTextureAttachment(attachments.get(Attachment.DEPTH).getHandle(), (sample > 1));
              } else {
                getLog().debug("Create depth texture attachment.");
                frameBuffer.createDepthTextureAttachment(attachments.get(Attachment.DEPTH).getHandle());
              }
            }
          });

      frameBuffer.setDrawBuffers(drawBuffers);
      frameBuffer.checkStatus();
      frameBuffer.unbind();
    }
  }

  public GLTexture getAttachmentTexture(final Attachment attachment) {
    return attachments.get(attachment);
  }

  public void bind() {
    frameBuffer.bind();
  }

  public void unbind() {
    frameBuffer.unbind();
  }

  protected abstract Map<Attachment, GLTexture> configureAttachments(int sample);

  protected abstract Logger getLog();
}
