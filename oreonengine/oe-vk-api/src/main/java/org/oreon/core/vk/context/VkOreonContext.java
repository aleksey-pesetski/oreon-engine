package org.oreon.core.vk.context;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.BaseOreonRenderEngine;
import org.oreon.core.CoreEngine;
import org.oreon.core.context.BaseOreonContext;
import org.oreon.core.context.Config;
import org.oreon.core.platform.Input;
import org.oreon.core.vk.platform.VkWindow;
import org.oreon.core.vk.scenegraph.VkCamera;

@Log4j2
@Getter
public class VkOreonContext extends BaseOreonContext<Input, VkCamera, VkWindow, VkResources> {

  private DeviceManager deviceManager;
  private VulkanInstance vkInstance;
  private long surface;

  public VkOreonContext(
      final Input input,
      final Config config,
      final VkCamera camera,
      final VkWindow window,
      final VkResources resources,
      final BaseOreonRenderEngine renderEngine,
      final CoreEngine coreEngine, final DeviceManager deviceManager,
      final VulkanInstance vkInstance, final long surface) {
    super(input, camera, window, resources, config, renderEngine, coreEngine);
    this.deviceManager = deviceManager;
    this.surface = surface;
    this.vkInstance = vkInstance;
  }
}
