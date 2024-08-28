
package org.oreon.examples.vk.oreonworlds;

import org.oreon.core.context.Config;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.vk.context.VkOreonContext;
import org.oreon.core.vk.context.VkResources;
import org.oreon.core.vk.platform.VkWindow;
import org.oreon.core.vk.scenegraph.VkCamera;
import org.oreon.vk.components.atmosphere.Atmosphere;
import org.oreon.vk.components.water.Water;
import org.oreon.vk.engine.VkDeferredEngine;

public class VkOreonworlds {

  public static void main(String[] args) {
    final var config = new Config();
    final var vkCamera = new VkCamera(config);
    final var vkWindow = new VkWindow(config);
    final var vkResources = new VkResources();
    ContextHolder.setContext(new VkOreonContext(config, vkCamera, vkWindow, vkResources));

    VkDeferredEngine renderEngine = new VkDeferredEngine(config, vkCamera);
    renderEngine.setGui(new VkSystemMonitor());
    renderEngine.init();

    renderEngine.getSceneGraph().setWater(new Water());
    renderEngine.getSceneGraph().addObject(new Atmosphere());
//		renderEngine.getSceneGraph().setTerrain(new Planet());

    ContextHolder.getContext().setRenderEngine(renderEngine);
    ContextHolder.getContext().getCoreEngine().start();
  }
}
