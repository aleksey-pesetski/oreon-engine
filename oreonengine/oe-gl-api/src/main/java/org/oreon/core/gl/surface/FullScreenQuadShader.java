package org.oreon.core.gl.surface;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.ResourceLoaderUtils;

public class FullScreenQuadShader extends GLShaderProgram {

  private static FullScreenQuadShader instance = null;

  public static FullScreenQuadShader getInstance() {
    if (instance == null) {
      instance = new FullScreenQuadShader();
    }
    return instance;
  }

  private FullScreenQuadShader() {
    super();

    addVertexShader(ResourceLoaderUtils.load("shaders/quad/quad_VS.glsl"));
    addFragmentShader(ResourceLoaderUtils.load("shaders/quad/quad_FS.glsl"));
    compileShader();

    addUniform("texture");
  }

  public void updateUniforms(GLTexture texture) {
    glActiveTexture(GL_TEXTURE0);
    texture.bind();
    setUniformi("texture", 0);
  }
}
