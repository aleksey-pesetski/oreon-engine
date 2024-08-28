package org.oreon.gl.components.filter.bloom;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoaderUtils;

public class BloomSceneBlendingShader extends GLShaderProgram {

  private static BloomSceneBlendingShader instance = null;

  public static BloomSceneBlendingShader getInstance() {
    if (instance == null) {
      instance = new BloomSceneBlendingShader();
    }
    return instance;
  }

  protected BloomSceneBlendingShader() {
    super();

    addComputeShader(ResourceLoaderUtils.load("shaders/filter/bloom/bloomSceneBlending.comp"));

    compileShader();
  }
}
