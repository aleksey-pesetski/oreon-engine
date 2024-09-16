package org.oreon.core.vk.framebuffer;

import static org.lwjgl.system.MemoryUtil.memAllocLong;

import java.util.Comparator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.Getter;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.target.Attachment;
import org.oreon.core.target.FrameBufferObject;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.wrapper.image.VkImageBundle;

@Getter
public abstract class VkFrameBufferObject extends FrameBufferObject {

  private final VkDevice device;
  private final RenderPass renderPass;
  private final Map<Attachment, VkImageBundle> attachments;

  private final VkFrameBuffer frameBuffer;

  protected VkFrameBufferObject(final int width, final int height, final int depthAttachmentCount,
      final VkDevice device,
      final BiFunction<Integer, Integer, Map<Attachment, VkImageBundle>> attachmentsFunction,
      final Function<RenderPass, RenderPass> passRenderPassFunction) {
    super(width, height, depthAttachmentCount);
    this.device = device;
    this.attachments = attachmentsFunction.apply(getWidth(), getHeight());
    configureAttachments(getWidth(), getHeight());
    //this.renderPass = configureRenderPass(createRenderPass(this.device));
    this.renderPass = passRenderPassFunction.apply(createRenderPass(getDevice()));

    setColorAttachmentCount(renderPass.getAttachmentCount() - depthAttachmentCount);

    var attachmentTypes = attachments.keySet().stream()
        .sorted(Comparator.comparingInt(Attachment::getOrder))
        .toList();

    final var pImageViews = memAllocLong(renderPass.getAttachmentCount());
    IntStream.range(0, attachmentTypes.size())
        .forEach(
            index -> pImageViews.put(index, attachments.get(attachmentTypes.get(index)).getImageView().getHandle())
        );

    this.frameBuffer = new VkFrameBuffer(device, getWidth(), getHeight(), 1, pImageViews, renderPass.getHandle());
  }


  public VkImageView getAttachmentImageView(Attachment type) {
    return attachments.get(type).getImageView();
  }

  public void destroy() {
    frameBuffer.destroy();
    renderPass.destroy();

    attachments.forEach((key, value) -> value.destroy());
  }

  /**
   * Creates {@link RenderPass}. P.S.: If someone need to override and setup custom.
   *
   * @param device - the {@link VkDevice} instance.
   * @return the {@link RenderPass} instance.
   */
  protected RenderPass createRenderPass(final VkDevice device) {
    return new RenderPass(device);
  }

  protected abstract Map<Attachment, VkImageBundle> configureAttachments(final int width, final int height);

  protected abstract RenderPass configureRenderPass(final RenderPass renderPass);
}
