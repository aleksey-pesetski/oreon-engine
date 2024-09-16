package org.oreon.gl.components.filter.gaussianblur;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoaderUtils;

public class GaussianBlurHorizontalShader extends GLShaderProgram {

  private static GaussianBlurHorizontalShader instance = null;

  public static GaussianBlurHorizontalShader getInstance() {
    if (instance == null) {
      instance = new GaussianBlurHorizontalShader();
    }
    return instance;
  }

  protected GaussianBlurHorizontalShader() {
    super();

    addComputeShader(ResourceLoaderUtils.load("shaders/filter/gaussian_blur/horizontal_gaussian_blur.comp"));

    compileShader();
  }

}
