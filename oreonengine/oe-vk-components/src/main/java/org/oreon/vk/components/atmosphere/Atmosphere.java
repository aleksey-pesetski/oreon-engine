package org.oreon.vk.components.atmosphere;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;
import static org.oreon.core.model.VertexLayout.POS;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.Config;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ProceduralTexturing;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.context.DeviceManager;
import org.oreon.core.vk.context.DeviceManager.DeviceType;
import org.oreon.core.vk.context.VkDescriptorName;
import org.oreon.core.vk.context.VkResources;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.memory.VkBuffer;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;
import org.oreon.core.vk.scenegraph.VkCamera;
import org.oreon.core.vk.scenegraph.VkMeshData;
import org.oreon.core.vk.scenegraph.VkRenderInfo;
import org.oreon.core.vk.util.VkAssimpModelLoader;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.command.SecondaryDrawIndexedCmdBuffer;
import org.oreon.core.vk.wrapper.pipeline.GraphicsPipeline;

public class Atmosphere extends Renderable {

  private VkUniformBuffer uniformBuffer;

  public Atmosphere(Config config, VkCamera vkCamera, VkResources vkResources, DeviceManager deviceManager) {
    LogicalDevice device = deviceManager.getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE);
    VkPhysicalDeviceMemoryProperties memoryProperties =
        deviceManager.getPhysicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getMemoryProperties();

    getWorldTransform().setLocalScaling(Constants.ZFAR * 0.5f, Constants.ZFAR * 0.5f, Constants.ZFAR * 0.5f);

    Mesh mesh = VkAssimpModelLoader.loadModel("models/obj/dome", "dome.obj").get(0).getMesh();
    ProceduralTexturing.dome(mesh);

    ByteBuffer ubo = memAlloc(Float.BYTES * 16);
    ubo.put(BufferUtil.createByteBuffer(getWorldTransform().getWorldMatrix()));
    ubo.flip();

    uniformBuffer = new VkUniformBuffer(device.getHandle(), memoryProperties, ubo);

    ShaderModule vertexShader = new ShaderModule(device.getHandle(),
        "shaders/atmosphere/atmosphere.vert.spv", VK_SHADER_STAGE_VERTEX_BIT);

    ShaderPipeline graphicsShaderPipeline = new ShaderPipeline(device.getHandle());
    graphicsShaderPipeline.addShaderModule(vertexShader);
    graphicsShaderPipeline.createFragmentShader(config.isAtmosphericScatteringEnable() ?
        "shaders/atmosphere/atmospheric_scattering.frag.spv" : "shaders/atmosphere/atmosphere.frag.spv");
    graphicsShaderPipeline.createShaderPipeline();

    ShaderPipeline reflectionShaderPipeline = new ShaderPipeline(device.getHandle());
    reflectionShaderPipeline.addShaderModule(vertexShader);
    reflectionShaderPipeline.createFragmentShader("shaders/atmosphere/atmospheric_scattering_reflection.frag.spv");
    reflectionShaderPipeline.createShaderPipeline();

    List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
    List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();

    DescriptorSetLayout descriptorSetLayout = new DescriptorSetLayout(device.getHandle(), 1);
    descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
        VK_SHADER_STAGE_VERTEX_BIT);
    descriptorSetLayout.create();
    DescriptorSet descriptorSet = new DescriptorSet(device.getHandle(),
        device.getDescriptorPool(Thread.currentThread().getId()).getHandle(),
        descriptorSetLayout.getHandlePointer());
    descriptorSet.updateDescriptorBuffer(uniformBuffer.getHandle(),
        ubo.limit(), 0, 0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);

    descriptorSets.add(vkCamera.getDescriptorSet());
    descriptorSets.add(descriptorSet);
    descriptorSets.add(
        vkResources.getDescriptors().get(VkDescriptorName.DIRECTIONAL_LIGHT).getDescriptorSet());
    descriptorSetLayouts.add(vkCamera.getDescriptorSetLayout());
    descriptorSetLayouts.add(descriptorSetLayout);
    descriptorSetLayouts.add(
        vkResources.getDescriptors().get(VkDescriptorName.DIRECTIONAL_LIGHT).getDescriptorSetLayout());

    VkVertexInput vertexInput = new VkVertexInput(POS);

    ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(mesh.getVertices(), POS);
    ByteBuffer indexBuffer = BufferUtil.createByteBuffer(mesh.getIndices());

    int pushConstantsRange = Float.BYTES * 20 + Integer.BYTES * 3;

    ByteBuffer pushConstants = memAlloc(pushConstantsRange);
    pushConstants.put(BufferUtil.createByteBuffer(vkCamera.getProjectionMatrix()));
    pushConstants.putFloat(config.getSunRadius());
    pushConstants.putInt(config.getFrameWidth());
    pushConstants.putInt(config.getFrameHeight());
    pushConstants.putInt(0);
    pushConstants.putFloat(config.getAtmosphereBloomFactor());
    pushConstants.putFloat(config.getHorizonVerticalShift());
    pushConstants.putFloat(config.getHorizonReflectionVerticalShift());
    pushConstants.flip();

    VkPipeline graphicsPipeline = new GraphicsPipeline(device.getHandle(),
        graphicsShaderPipeline, vertexInput, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
        VkUtil.createLongBuffer(descriptorSetLayouts),
        config.getFrameWidth(),
        config.getFrameHeight(),
        vkResources.getOffScreenFbo().getRenderPass().getHandle(),
        vkResources.getOffScreenFbo().getColorAttachmentCount(),
        config.getMultisampling_sampleCount(),
        pushConstantsRange, VK_SHADER_STAGE_FRAGMENT_BIT);

    VkBuffer vertexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
        device.getHandle(), memoryProperties,
        device.getTransferCommandPool(Thread.currentThread().getId()).getHandle(),
        device.getTransferQueue(),
        vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

    VkBuffer indexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
        device.getHandle(), memoryProperties,
        device.getTransferCommandPool(Thread.currentThread().getId()).getHandle(),
        device.getTransferQueue(),
        indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);

    CommandBuffer mainCommandBuffer = new SecondaryDrawIndexedCmdBuffer(
        device.getHandle(),
        device.getGraphicsCommandPool(Thread.currentThread().getId()).getHandle(),
        graphicsPipeline.getHandle(), graphicsPipeline.getLayoutHandle(),
        vkResources.getOffScreenFbo().getFrameBuffer().getHandle(),
        vkResources.getOffScreenFbo().getRenderPass().getHandle(),
        0,
        VkUtil.createLongArray(descriptorSets),
        vertexBufferObject.getHandle(),
        indexBufferObject.getHandle(),
        mesh.getIndices().length,
        pushConstants, VK_SHADER_STAGE_FRAGMENT_BIT);

    VkMeshData meshData = VkMeshData.builder().vertexBufferObject(vertexBufferObject)
        .vertexBuffer(vertexBuffer).indexBufferObject(indexBufferObject).indexBuffer(indexBuffer)
        .build();
    VkRenderInfo mainRenderInfo = VkRenderInfo.builder().commandBuffer(mainCommandBuffer)
        .pipeline(graphicsPipeline).descriptorSets(descriptorSets)
        .descriptorSetLayouts(descriptorSetLayouts).build();

    addComponent(NodeComponentType.MESH_DATA, meshData);
    addComponent(NodeComponentType.MAIN_RENDERINFO, mainRenderInfo);
    addComponent(NodeComponentType.WIREFRAME_RENDERINFO, mainRenderInfo);

    if (vkResources.getReflectionFbo() != null) {
      VkPipeline reflectionPipeline = new GraphicsPipeline(device.getHandle(),
          reflectionShaderPipeline, vertexInput, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
          VkUtil.createLongBuffer(descriptorSetLayouts),
          vkResources.getReflectionFbo().getWidth(),
          vkResources.getReflectionFbo().getHeight(),
          vkResources.getReflectionFbo().getRenderPass().getHandle(),
          vkResources.getReflectionFbo().getColorAttachmentCount(), 1,
          pushConstantsRange, VK_SHADER_STAGE_FRAGMENT_BIT);

      CommandBuffer reflectionCommandBuffer = new SecondaryDrawIndexedCmdBuffer(
          device.getHandle(),
          device.getGraphicsCommandPool(Thread.currentThread().getId()).getHandle(),
          reflectionPipeline.getHandle(), reflectionPipeline.getLayoutHandle(),
          vkResources.getReflectionFbo().getFrameBuffer().getHandle(),
          vkResources.getReflectionFbo().getRenderPass().getHandle(),
          0,
          VkUtil.createLongArray(descriptorSets),
          vertexBufferObject.getHandle(),
          indexBufferObject.getHandle(),
          mesh.getIndices().length,
          pushConstants, VK_SHADER_STAGE_FRAGMENT_BIT);

      VkRenderInfo reflectionRenderInfo = VkRenderInfo.builder().commandBuffer(reflectionCommandBuffer)
          .pipeline(reflectionPipeline).build();

      addComponent(NodeComponentType.REFLECTION_RENDERINFO, reflectionRenderInfo);
    }

    graphicsShaderPipeline.destroy();
    reflectionShaderPipeline.destroy();
  }

  public void update() {
    super.update();

    uniformBuffer.mapMemory(BufferUtil.createByteBuffer(getWorldTransform().getWorldMatrix()));
  }

  public void shutdown() {

    super.shutdown();
    uniformBuffer.destroy();
  }

}
