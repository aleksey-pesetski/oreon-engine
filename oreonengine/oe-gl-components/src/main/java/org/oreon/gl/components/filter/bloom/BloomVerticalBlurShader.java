package org.oreon.gl.components.filter.bloom;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoaderUtils;

public class BloomVerticalBlurShader extends GLShaderProgram {

  private static BloomVerticalBlurShader instance = null;

  public static BloomVerticalBlurShader getInstance() {
    if (instance == null) {
      instance = new BloomVerticalBlurShader();
    }
    return instance;
  }

  protected BloomVerticalBlurShader() {
    super();

    addComputeShader(ResourceLoaderUtils.load("shaders/filter/bloom/bloom_verticalGaussianBlur.comp"));
    compileShader();
  }
}
