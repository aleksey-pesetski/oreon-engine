package org.oreon.gl.components.filter.bloom;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoaderUtils;

public class SceneBrightnessShader extends GLShaderProgram {

  private static SceneBrightnessShader instance = null;

  public static SceneBrightnessShader getInstance() {
    if (instance == null) {
      instance = new SceneBrightnessShader();
    }
    return instance;
  }

  protected SceneBrightnessShader() {
    super();

    addComputeShader(ResourceLoaderUtils.load("shaders/filter/bloom/brightness.comp"));
    compileShader();
  }
}
