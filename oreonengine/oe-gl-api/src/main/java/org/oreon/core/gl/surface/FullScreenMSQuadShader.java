package org.oreon.core.gl.surface;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.ContextHolder;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoaderUtils;

public class FullScreenMSQuadShader extends GLShaderProgram {

  private static FullScreenMSQuadShader instance = null;

  private FullScreenMSQuadShader() {
    super();

    addVertexShader(ResourceLoaderUtils.loadShader("shaders/quad/quad_VS.glsl"));
    addFragmentShader(ResourceLoaderUtils.loadShader("shaders/quad/quadMS_FS.glsl"));
    compileShader();

    addUniform("texture");
    addUniform("width");
    addUniform("height");
    addUniform("multisamples");
  }

  public static FullScreenMSQuadShader getInstance() {
    if (instance == null) {
      instance = new FullScreenMSQuadShader();
    }
    return instance;
  }

  public void updateUniforms(GLTexture texture) {
    glActiveTexture(GL_TEXTURE0);
    texture.bind();
    setUniformi("texture", 0);

    setUniformi("width", ContextHolder.getContext().getConfig().getFrameWidth());
    setUniformi("height", ContextHolder.getContext().getConfig().getFrameHeight());
    setUniformi("multisamples", ContextHolder.getContext().getConfig().getMultisampling_sampleCount());
  }
}
