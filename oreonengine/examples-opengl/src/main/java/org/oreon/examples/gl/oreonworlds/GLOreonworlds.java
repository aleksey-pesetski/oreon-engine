package org.oreon.examples.gl.oreonworlds;

import lombok.extern.log4j.Log4j2;
import org.oreon.core.CoreEngine;
import org.oreon.core.context.Config;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.gl.context.GLOreonContext;
import org.oreon.core.gl.context.GLResources;
import org.oreon.core.gl.platform.GLWindow;
import org.oreon.core.gl.scenegraph.GLCamera;
import org.oreon.core.platform.GLFWInput;
import org.oreon.examples.gl.oreonworlds.ocean.Ocean;
import org.oreon.examples.gl.oreonworlds.terrain.Terrain;
import org.oreon.gl.components.atmosphere.Atmosphere;
import org.oreon.gl.components.terrain.shader.TerrainShader;
import org.oreon.gl.components.terrain.shader.TerrainShadowShader;
import org.oreon.gl.components.terrain.shader.TerrainWireframeShader;
import org.oreon.gl.engine.GLDeferredEngine;

@Log4j2
public class GLOreonworlds {

  public static void main(String[] args) {

    try {
      var input = new GLFWInput();
      var config = new Config();
      var window = new GLWindow(config);
      var camera = new GLCamera(config, input, window);
      var resources = new GLResources();
      var renderEngine = new GLDeferredEngine(config, camera, resources);
      var coreEngine = new CoreEngine(window, input, renderEngine);

      //		renderEngine.setGui(new GLSystemMonitor());
      renderEngine.init();
      renderEngine.getSceneGraph().addObject(new Atmosphere());
      renderEngine.getSceneGraph().setWater(new Ocean());
      renderEngine.getSceneGraph().setTerrain(
          new Terrain(
              TerrainShader.getInstance(),
              TerrainWireframeShader.getInstance(),
              TerrainShadowShader.getInstance()
          )
      );
      coreEngine.start();

      //		renderEngine.getSceneGraph().getRoot().addChild(new Palm01ClusterGroup());
      //		renderEngine.getSceneGraph().getRoot().addChild(new Plant01ClusterGroup());
      //		renderEngine.getSceneGraph().getRoot().addChild(new Grass01ClusterGroup());
      //		renderEngine.getSceneGraph().getRoot().addChild(new Tree02ClusterGroup());
      //		renderEngine.getSceneGraph().getRoot().addChild(new Tree01ClusterGroup());
      //		renderEngine.getSceneGraph().getRoot().addChild(new Rock01ClusterGroup());
      //		renderEngine.getSceneGraph().getRoot().addChild(new Rock02ClusterGroup());

      ContextHolder.setContext(new GLOreonContext(config, input, resources, window, camera, renderEngine, coreEngine));
    } catch (Exception e) {
      log.error(e);
      System.exit(1);
    }
  }
}
