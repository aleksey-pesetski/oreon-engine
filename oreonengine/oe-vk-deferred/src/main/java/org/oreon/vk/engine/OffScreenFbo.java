package org.oreon.vk.engine;

import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_SHADER_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import java.util.Map;
import lombok.Getter;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.Config;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.target.Attachment;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.FrameBufferDepthAttachment;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.wrapper.image.VkImageBundle;

@Getter
public class OffScreenFbo extends VkFrameBufferObject {

  public OffScreenFbo(Config config, VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties) {
    super(
        config.getFrameWidth(),
        config.getFrameHeight(),
        1,
        device,
        (width, height) -> {
          final int samples = config.getMultisampling_sampleCount();
          return Map.of(
              Attachment.COLOR, new FrameBufferColorAttachment(device, memoryProperties,
                  width, height, VK_FORMAT_R16G16B16A16_SFLOAT, samples),
              Attachment.POSITION, new FrameBufferColorAttachment(device, memoryProperties,
                  width, height, VK_FORMAT_R32G32B32A32_SFLOAT, samples),
              Attachment.NORMAL, new FrameBufferColorAttachment(device, memoryProperties,
                  width, height, VK_FORMAT_R16G16B16A16_SFLOAT, samples),
              Attachment.LIGHT_SCATTERING, new FrameBufferColorAttachment(device, memoryProperties,
                  width, height, VK_FORMAT_R16G16B16A16_SFLOAT, samples),
              Attachment.SPECULAR_EMISSION_DIFFUSE_SSAO_BLOOM, new FrameBufferColorAttachment(device, memoryProperties,
                  width, height, VK_FORMAT_R16G16B16A16_SFLOAT, samples),
              Attachment.DEPTH, new FrameBufferDepthAttachment(device, memoryProperties,
                  width, height, VK_FORMAT_D32_SFLOAT, samples)
          );
        },
        (renderPass) -> {
          final int samples = config.getMultisampling_sampleCount();

          renderPass.addColorAttachment(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
              VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
              VK_IMAGE_LAYOUT_GENERAL);
          renderPass.addColorAttachment(1, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
              VK_FORMAT_R32G32B32A32_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
              VK_IMAGE_LAYOUT_GENERAL);
          renderPass.addColorAttachment(2, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
              VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
              VK_IMAGE_LAYOUT_GENERAL);
          renderPass.addColorAttachment(3, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
              VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
              VK_IMAGE_LAYOUT_GENERAL);
          renderPass.addColorAttachment(4, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
              VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
              VK_IMAGE_LAYOUT_GENERAL);
          renderPass.addDepthAttachment(5, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
              VK_FORMAT_D32_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
              VK_IMAGE_LAYOUT_GENERAL);

          renderPass.addSubpassDependency(VK_SUBPASS_EXTERNAL, 0,
              VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
              VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
              VK_ACCESS_MEMORY_READ_BIT,
              VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT |
                  VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
              VK_DEPENDENCY_BY_REGION_BIT);
          renderPass.addSubpassDependency(0, VK_SUBPASS_EXTERNAL,
              VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
              VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT,
              VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT |
                  VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
              VK_ACCESS_SHADER_READ_BIT,
              VK_DEPENDENCY_BY_REGION_BIT);
          renderPass.createSubpass();
          renderPass.createRenderPass();

          return renderPass;
        }
    );

    /*final int samples = ContextHolder.getContext().getConfig().getMultisampling_sampleCount();
    getAttachments().put(Attachment.COLOR, new FrameBufferColorAttachment(device, memoryProperties,
        getWidth(), getHeight(), VK_FORMAT_R16G16B16A16_SFLOAT, samples));
    getAttachments().put(Attachment.POSITION, new FrameBufferColorAttachment(device, memoryProperties,
        getWidth(), getHeight(), VK_FORMAT_R32G32B32A32_SFLOAT, samples));
    getAttachments().put(Attachment.NORMAL, new FrameBufferColorAttachment(device, memoryProperties,
        getWidth(), getHeight(), VK_FORMAT_R16G16B16A16_SFLOAT, samples));
    getAttachments().put(Attachment.LIGHT_SCATTERING, new FrameBufferColorAttachment(device, memoryProperties,
        getWidth(), getHeight(), VK_FORMAT_R16G16B16A16_SFLOAT, samples));
    getAttachments().put(Attachment.SPECULAR_EMISSION_DIFFUSE_SSAO_BLOOM,
        new FrameBufferColorAttachment(device, memoryProperties,
            getWidth(), getHeight(), VK_FORMAT_R16G16B16A16_SFLOAT, samples));
    getAttachments().put(Attachment.DEPTH, new FrameBufferDepthAttachment(device, memoryProperties,
        getWidth(), getHeight(), VK_FORMAT_D32_SFLOAT, samples));*/
  }

  @Override
  protected Map<Attachment, VkImageBundle> configureAttachments(int width, int height) {
    return Map.of();
  }

  @Override
  protected RenderPass configureRenderPass(final RenderPass renderPass) {
    final int samples = ContextHolder.getContext().getConfig().getMultisampling_sampleCount();

    renderPass.addColorAttachment(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addColorAttachment(1, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R32G32B32A32_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addColorAttachment(2, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addColorAttachment(3, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addColorAttachment(4, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
        VK_FORMAT_R16G16B16A16_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);
    renderPass.addDepthAttachment(5, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
        VK_FORMAT_D32_SFLOAT, samples, VK_IMAGE_LAYOUT_UNDEFINED,
        VK_IMAGE_LAYOUT_GENERAL);

    renderPass.addSubpassDependency(VK_SUBPASS_EXTERNAL, 0,
        VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
        VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
        VK_ACCESS_MEMORY_READ_BIT,
        VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT |
            VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
        VK_DEPENDENCY_BY_REGION_BIT);
    renderPass.addSubpassDependency(0, VK_SUBPASS_EXTERNAL,
        VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
        VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT,
        VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT |
            VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
        VK_ACCESS_SHADER_READ_BIT,
        VK_DEPENDENCY_BY_REGION_BIT);
    renderPass.createSubpass();
    renderPass.createRenderPass();

    return renderPass;
  }
}
