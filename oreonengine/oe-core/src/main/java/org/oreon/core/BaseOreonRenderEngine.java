package org.oreon.core;

import lombok.Getter;
import org.oreon.core.context.Config;
import org.oreon.core.context.OreonRenderEngine;
import org.oreon.core.scenegraph.BaseOreonCamera;
import org.oreon.core.scenegraph.Scenegraph;

@Getter
public abstract class BaseOreonRenderEngine implements OreonRenderEngine {

  private Scenegraph sceneGraph;
  private Config config;
  private BaseOreonCamera camera;

  public BaseOreonRenderEngine(Config config, BaseOreonCamera camera) {
    this.config = config;
    this.camera = camera;
    this.sceneGraph = new Scenegraph();
  }

  @Override
  public void init() {
    camera.init();
  }

  @Override
  public void update() {
    camera.update();
    sceneGraph.update();
    sceneGraph.updateLights();
  }

  @Override
  public void shutdown() {

    // important to shutdown scenegraph before render-engine, since
    // thread safety of instancing clusters.
    // scenegraph sets isRunning to false, render-engine signals all
    // waiting threads to shutdown
    sceneGraph.shutdown();
  }

}
