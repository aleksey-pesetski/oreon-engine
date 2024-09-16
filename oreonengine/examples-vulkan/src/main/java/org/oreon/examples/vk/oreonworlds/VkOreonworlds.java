
package org.oreon.examples.vk.oreonworlds;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import lombok.extern.log4j.Log4j2;
import org.oreon.core.CoreEngine;
import org.oreon.core.context.Config;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.vk.context.DeviceManager;
import org.oreon.core.vk.context.DeviceManager.DeviceType;
import org.oreon.core.vk.context.VkOreonContext;
import org.oreon.core.vk.context.VkResources;
import org.oreon.core.vk.context.VulkanInstance;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.PhysicalDevice;
import org.oreon.core.vk.device.VkDeviceBundle;
import org.oreon.core.vk.platform.VkWindow;
import org.oreon.core.vk.scenegraph.VkCamera;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.vk.components.atmosphere.Atmosphere;
import org.oreon.vk.components.water.Water;
import org.oreon.vk.engine.VkDeferredEngine;

@Log4j2
public class VkOreonworlds {

  public static void main(String[] args) {
    log.info("Starting vk...");

    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    if (!glfwVulkanSupported()) {
      throw new AssertionError("GLFW failed to find the Vulkan loader");
    }

    final var input = new GLFWInput();
    final var config = new Config();
    final var vkResources = new VkResources();
    final var deviceManager = new DeviceManager();
    final var vkWindow = new VkWindow(config);

    final var enabledLayers = new ByteBuffer[]{
        memUTF8("VK_LAYER_LUNARG_standard_validation")
        //, memUTF8("VK_LAYER_LUNARG_assistant_layer")
    };
    final var vkInstance = new VulkanInstance(
        VkUtil.getValidationLayerNames(config.isVkValidation(), enabledLayers)
    );

    vkWindow.create();

    LongBuffer pSurface = memAllocLong(1);
    int err = glfwCreateWindowSurface(vkInstance.getHandle(), vkWindow.getId(), null, pSurface);
    long surface = pSurface.get(0);
    if (err != VK_SUCCESS) {
      throw new AssertionError("Failed to create surface: " + VkUtil.translateVulkanResult(err));
    }
    PhysicalDevice physicalDevice = new PhysicalDevice(vkInstance.getHandle(), surface);
    LogicalDevice logicalDevice = new LogicalDevice(physicalDevice, 0);
    VkDeviceBundle majorDevice = new VkDeviceBundle(physicalDevice, logicalDevice);
    deviceManager.addDevice(DeviceType.MAJOR_GRAPHICS_DEVICE, majorDevice);

    DescriptorPool descriptorPool = new DescriptorPool(
        majorDevice.getLogicalDevice().getHandle(), 4);
    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 33);
    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, 61);
    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 2);
    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 12);
    descriptorPool.create();
    majorDevice.getLogicalDevice().addDescriptorPool(Thread.currentThread().getId(), descriptorPool);

    final var vkCamera = new VkCamera(config, input, vkWindow, deviceManager, vkResources);

    ContextHolder.setContext(
        new VkOreonContext(
            input, config, vkCamera, vkWindow, vkResources,
            null, null, deviceManager,
            vkInstance, surface));
    final var renderEngine = new VkDeferredEngine(config, vkCamera, deviceManager, vkResources);
    renderEngine.setGui(new VkSystemMonitor());
    renderEngine.getSceneGraph().setWater(new Water(config, vkCamera, vkResources, deviceManager));
    renderEngine.getSceneGraph().addObject(new Atmosphere(config, vkCamera, vkResources, deviceManager));
//		renderEngine.getSceneGraph().setTerrain(new Planet());

    final var coreEngine = new CoreEngine(vkWindow, input, renderEngine);

    ContextHolder.setContext(
        new VkOreonContext(
            input, config, vkCamera, vkWindow, vkResources,
            renderEngine, coreEngine, deviceManager,
            vkInstance, surface));

    renderEngine.init();
    coreEngine.start();
  }
}
