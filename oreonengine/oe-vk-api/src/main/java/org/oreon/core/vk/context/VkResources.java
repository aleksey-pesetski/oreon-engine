package org.oreon.core.vk.context;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.oreon.core.context.OreonResource;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;

@Getter
@Setter
public class VkResources implements OreonResource {

  private VkFrameBufferObject offScreenFbo;
  private VkFrameBufferObject reflectionFbo;
  private VkFrameBufferObject refractionFbo;
  private VkFrameBufferObject transparencyFbo;

  private Map<VkDescriptorName, VkDescriptor> descriptors = new HashMap<>();
}
