package org.oreon.examples.gl.oreonworlds.shaders.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoaderUtils;

public class TreeShadowShader extends GLShaderProgram {

  private static TreeShadowShader instance;

  public static TreeShadowShader getInstance() {
    if (instance == null) {
      instance = new TreeShadowShader();
    }
    return instance;
  }

  protected TreeShadowShader() {
    super();

    addVertexShader(ResourceLoaderUtils.load("oreonworlds/shaders/assets/Tree_Shader/TreeShadow_VS.glsl"));
    addGeometryShader(ResourceLoaderUtils.load("oreonworlds/shaders/assets/Tree_Shader/TreeShadow_GS.glsl"));
    addFragmentShader(ResourceLoaderUtils.load("oreonworlds/shaders/assets/Tree_Shader/TreeShadow_FS.glsl"));
    compileShader();

    addUniform("clipplane");
    addUniformBlock("worldMatrices");
    addUniformBlock("Camera");
    addUniformBlock("LightViewProjections");
    addUniform("material.diffusemap");

    for (int i = 0; i < 100; i++) {
      addUniform("matrixIndices[" + i + "]");
    }
  }

  public void updateUniforms(Renderable object) {

    setUniform("clipplane", ContextHolder.getContext().getConfig().getClipplane());
    bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
    bindUniformBlock("LightViewProjections", Constants.LightMatricesUniformBlockBinding);
    bindUniformBlock("worldMatrices", 0);

    Material material = object.getComponent(NodeComponentType.MATERIAL0);

    glActiveTexture(GL_TEXTURE0);
    material.getDiffusemap().bind();
    setUniformi("material.diffusemap", 0);

    InstancedObject vParentNode = object.getParentObject();
    List<Integer> indices = vParentNode.getHighPolyIndices();

    for (int i = 0; i < indices.size(); i++) {
      setUniformi("matrixIndices[" + i + "]", indices.get(i));
    }
  }
}
