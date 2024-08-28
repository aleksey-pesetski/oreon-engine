package org.oreon.gl.components.filter.motionblur;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoaderUtils;

public class MotionBlurShader extends GLShaderProgram {

  private static MotionBlurShader instance = null;

  public static MotionBlurShader getInstance() {
    if (instance == null) {
      instance = new MotionBlurShader();
    }
    return instance;
  }

  protected MotionBlurShader() {
    super();

    addComputeShader(ResourceLoaderUtils.load("shaders/filter/motion_blur/motionBlur.comp"));

    compileShader();

    addUniform("windowWidth");
    addUniform("windowHeight");
    addUniform("sceneSampler");
  }

  public void updateUniforms(int width, int height, GLTexture sceneSampler) {
    setUniformf("windowWidth", width);
    setUniformf("windowHeight", height);

    glActiveTexture(GL_TEXTURE0);
    sceneSampler.bind();
    setUniformi("sceneSampler", 0);
  }

}
