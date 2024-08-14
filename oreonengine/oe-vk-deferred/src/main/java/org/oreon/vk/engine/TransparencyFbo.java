package org.oreon.vk.engine;

import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import java.util.Map;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.target.Attachment;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.FrameBufferDepthAttachment;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.pipeline.RenderPass;

public class TransparencyFbo extends VkFrameBufferObject {

  public TransparencyFbo(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties) {
    super(
        ContextHolder.getContext().getConfig().getFrameWidth(),
        ContextHolder.getContext().getConfig().getFrameHeight(),
        1,
        device,
        (width, height) -> Map.of(
            Attachment.COLOR, new FrameBufferColorAttachment(device, memoryProperties,
                width, height, VK_FORMAT_R16G16B16A16_SFLOAT, 1),
            Attachment.ALPHA, new FrameBufferColorAttachment(device, memoryProperties,
                width, height, VK_FORMAT_R16G16B16A16_SFLOAT, 1),
            Attachment.LIGHT_SCATTERING, new FrameBufferColorAttachment(device, memoryProperties,
                width, height, VK_FORMAT_R16G16B16A16_SFLOAT, 1),
            Attachment.DEPTH, new FrameBufferDepthAttachment(device, memoryProperties,
                width, height, VK_FORMAT_D32_SFLOAT, 1)
        ));
  }

  @Override
  protected RenderPass configureRenderPass(final RenderPass renderPass) {
    renderPass.addColorAttachment(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R16G16B16A16_SFLOAT, 1, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addColorAttachment(1, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R16G16B16A16_SFLOAT, 1, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addColorAttachment(2, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R16G16B16A16_SFLOAT, 1, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addDepthAttachment(3, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
        VK_FORMAT_D32_SFLOAT, 1, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);

    renderPass.addSubpassDependency(VK_SUBPASS_EXTERNAL, 0,
        VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
        VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
        VK_ACCESS_MEMORY_READ_BIT,
        VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
        VK_DEPENDENCY_BY_REGION_BIT);
    renderPass.addSubpassDependency(0, VK_SUBPASS_EXTERNAL,
        VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
        VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
        VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
        VK_ACCESS_MEMORY_READ_BIT,
        VK_DEPENDENCY_BY_REGION_BIT);
    renderPass.createSubpass();
    renderPass.createRenderPass();
    return renderPass;
  }
}
