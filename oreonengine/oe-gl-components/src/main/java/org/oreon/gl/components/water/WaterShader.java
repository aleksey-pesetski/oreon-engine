package org.oreon.gl.components.water;

import org.oreon.common.water.WaterConfig;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoaderUtils;

import static org.lwjgl.opengl.GL13.*;

public class WaterShader extends GLShaderProgram {

  private static WaterShader instance = null;

  public static WaterShader getInstance() {
    if (instance == null) {
      instance = new WaterShader();
    }
    return instance;
  }

  protected WaterShader() {
    super();

    addVertexShader(ResourceLoaderUtils.load("shaders/water/water.vert"));
    addTessellationControlShader(ResourceLoaderUtils.load("shaders/water/water.tesc"));
    addTessellationEvaluationShader(ResourceLoaderUtils.load("shaders/water/water.tese"));
    addGeometryShader(ResourceLoaderUtils.load("shaders/water/water.geom"));
    addFragmentShader(ResourceLoaderUtils.load("shaders/water/water.frag"));
    compileShader();

    addUniform("waterReflection");
    addUniform("waterRefraction");
    addUniform("dudvMap");
    addUniform("distortion");

    addUniform("isCameraUnderWater");

    addUniform("normalmap");

    addUniform("Dy");
    addUniform("Dx");
    addUniform("Dz");
    addUniform("motion");
    addUniform("wind");

    addUniformBlock("Camera");
  }

  public void updateUniforms(Renderable object) {
    bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);

    Water ocean = (Water) object;
    WaterConfig configuration = ocean.getConfig();

    setUniformi("isCameraUnderWater", ContextHolder.getContext().getConfig().isRenderUnderwater() ? 1 : 0);
    setUniform("wind", configuration.getWindDirection());

    setUniformf("motion", ocean.getT_motion());
    setUniformf("distortion", ocean.getT_distortion());

    glActiveTexture(GL_TEXTURE0);
    ocean.getDudv().bind();
    setUniformi("dudvMap", 0);
    glActiveTexture(GL_TEXTURE1);
    ocean.getReflection_texture().bind();
    setUniformi("waterReflection", 1);
    glActiveTexture(GL_TEXTURE2);
    ocean.getRefraction_texture().bind();
    setUniformi("waterRefraction", 2);
    glActiveTexture(GL_TEXTURE3);
    ocean.getNormalmapRenderer().getNormalmap().bind();
    setUniformi("normalmap", 3);
    glActiveTexture(GL_TEXTURE4);
    ocean.getFft().getDy().bind();
    setUniformi("Dy", 4);
    glActiveTexture(GL_TEXTURE5);
    ocean.getFft().getDx().bind();
    setUniformi("Dx", 5);
    glActiveTexture(GL_TEXTURE6);
    ocean.getFft().getDz().bind();
    setUniformi("Dz", 6);
  }
}
