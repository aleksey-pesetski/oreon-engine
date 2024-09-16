package org.oreon.gl.components.ui;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.util.ResourceLoaderUtils;

public class UITexturePanelShader extends GLShaderProgram {

  private static UITexturePanelShader instance = null;

  public static UITexturePanelShader getInstance() {
    if (instance == null) {
      instance = new UITexturePanelShader();
    }
    return instance;
  }

  protected UITexturePanelShader() {
    super();

    addVertexShader(ResourceLoaderUtils.load("shaders/ui/texturePanel.vert"));
    addFragmentShader(ResourceLoaderUtils.load("shaders/ui/texturePanel.frag"));
    compileShader();

    addUniform("orthographicMatrix");
    addUniform("texture");
  }

  public void updateUniforms(Matrix4f orthographicMatrix) {
    setUniform("orthographicMatrix", orthographicMatrix);
  }

  public void updateUniforms(int texture) {
    setUniformi("texture", texture);
  }
}
