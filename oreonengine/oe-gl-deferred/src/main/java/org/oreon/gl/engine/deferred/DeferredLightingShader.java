package org.oreon.gl.engine.deferred;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.ContextHolder;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoaderUtils;

public class DeferredLightingShader extends GLShaderProgram {

  private static DeferredLightingShader instance = null;

  protected DeferredLightingShader() {
    super();

    addComputeShader(ResourceLoaderUtils.loadShader("shaders/deferredLighting.comp", "lib.glsl"));
    compileShader();

    addUniformBlock("Camera");
    addUniformBlock("DirectionalLight");
    addUniformBlock("DirectionalLightViewProjections");
    addUniform("numSamples");
    addUniform("pssm");
    addUniform("sightRangeFactor");
    addUniform("fogColor");
    addUniform("shadowsEnable");
    addUniform("shadowsQuality");
    addUniform("ssaoEnable");
  }

  public static DeferredLightingShader getInstance() {
    if (instance == null) {
      instance = new DeferredLightingShader();
    }
    return instance;
  }

  public void updateUniforms(GLTexture pssm) {

    bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
    bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
    bindUniformBlock("DirectionalLightViewProjections", Constants.LightMatricesUniformBlockBinding);
    setUniformf("sightRangeFactor", ContextHolder.getContext().getConfig().getSightRange());
    setUniform("fogColor", ContextHolder.getContext().getConfig().getFogColor());
    setUniformi("shadowsEnable", ContextHolder.getContext().getConfig().isShadowsEnable() ? 1 : 0);
    setUniformi("shadowsQuality", ContextHolder.getContext().getConfig().getShadowsQuality());

    if (ContextHolder.getContext().getConfig().isShadowsEnable()) {
      glActiveTexture(GL_TEXTURE0);
      pssm.bind();
      setUniformi("pssm", 0);
    }

    setUniformi("ssaoEnable", ContextHolder.getContext().getConfig().isSsaoEnabled() ? 1 : 0);
    setUniformi("numSamples", ContextHolder.getContext().getConfig().getMultisampling_sampleCount());
  }
}
