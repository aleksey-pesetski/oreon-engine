package org.oreon.examples.gl.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoaderUtils;

public class GrassShadowShader extends GLShaderProgram {

  private static GrassShadowShader instance;

  public static GrassShadowShader getInstance() {
    if (instance == null) {
      instance = new GrassShadowShader();
    }
    return instance;
  }

  protected GrassShadowShader() {
    super();

    addVertexShader(ResourceLoaderUtils.loadShader("oreonworlds/shaders/assets/Grass_Shader/GrassShadow_VS.glsl"));
    addGeometryShader(ResourceLoaderUtils.loadShader("oreonworlds/shaders/assets/Grass_Shader/GrassShadow_GS.glsl"));
    addFragmentShader(ResourceLoaderUtils.loadShader("oreonworlds/shaders/assets/Grass_Shader/GrassShadow_FS.glsl"));
    compileShader();

    addUniformBlock("worldMatrices");
    addUniformBlock("Camera");
    addUniformBlock("LightViewProjections");
    addUniform("material.diffusemap");

    for (int i = 0; i < 100; i++) {
      addUniform("matrixIndices[" + i + "]");
    }
  }

  public void updateUniforms(Renderable object) {

    bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
    bindUniformBlock("LightViewProjections", Constants.LightMatricesUniformBlockBinding);
    bindUniformBlock("worldMatrices", 0);

    Material material = object.getComponent(NodeComponentType.MATERIAL0);

    glActiveTexture(GL_TEXTURE0);
    material.getDiffusemap().bind();
    setUniformi("material.diffusemap", 0);

    InstancedObject vParentNode = object.getParentObject();
    List<Integer> indices = vParentNode.getLowPolyIndices();

    for (int i = 0; i < indices.size(); i++) {
      setUniformi("matrixIndices[" + i + "]", indices.get(i));
    }
  }

}
