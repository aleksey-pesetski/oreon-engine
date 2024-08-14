package org.oreon.gl.components.terrain.shader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.common.quadtree.ChunkConfig;
import org.oreon.common.quadtree.QuadtreeNode;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.gl.context.GLOreonContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoaderUtils;
import org.oreon.gl.components.terrain.GLTerrainConfig;

public class TerrainShader extends GLShaderProgram {

  private static final String SHADOW_MAP_RESOLUTION_PLACEHOLDER = "#var_shadow_map_resolution";
  private static final String LIB_GLSL_PLACEHOLDER = "#lib.glsl";

  private static TerrainShader instance = null;

  protected TerrainShader() {
    super();

    final String vLib = ResourceLoaderUtils.loadShader("shader/lib.glsl")
        .replaceFirst(
            SHADOW_MAP_RESOLUTION_PLACEHOLDER,
            Integer.toString(ContextHolder.getContext().getConfig().getShadowMapResolution())
        );

    addVertexShader(ResourceLoaderUtils.loadShader("shaders/terrain/terrain.vert")
        .replaceFirst(LIB_GLSL_PLACEHOLDER, vLib));
    addTessellationControlShader(ResourceLoaderUtils.loadShader("shaders/terrain/terrain.tesc")
        .replaceFirst(LIB_GLSL_PLACEHOLDER, vLib));
    addTessellationEvaluationShader(ResourceLoaderUtils.loadShader("shaders/terrain/terrain.tese")
        .replaceFirst(LIB_GLSL_PLACEHOLDER, vLib));
    addGeometryShader(ResourceLoaderUtils.loadShader("shaders/terrain/terrain.geom")
        .replaceFirst(LIB_GLSL_PLACEHOLDER, vLib));
    addFragmentShader(ResourceLoaderUtils.loadShader("shaders/terrain/terrain.frag")
        .replaceFirst(LIB_GLSL_PLACEHOLDER, vLib));
    compileShader();

    addUniform("localMatrix");
    addUniform("worldMatrix");

    addUniform("index");
    addUniform("gap");
    addUniform("lod");
    addUniform("location");
    addUniform("isRefraction");
    addUniform("isReflection");
    addUniform("isCameraUnderWater");

    addUniform("caustics");
    addUniform("dudvCaustics");
    addUniform("distortionCaustics");
    addUniform("underwaterBlurFactor");

    addUniform("heightmap");
    addUniform("normalmap");
    addUniform("splatmap");
    addUniform("yScale");
    addUniform("reflectionOffset");

    for (int i = 0; i < 3; i++) {
      addUniform("materials[" + i + "].diffusemap");
      addUniform("materials[" + i + "].normalmap");
      addUniform("materials[" + i + "].heightmap");
      addUniform("materials[" + i + "].heightScaling");
      addUniform("materials[" + i + "].uvScaling");
    }

    addUniform("clipplane");

    addUniformBlock("Camera");
    addUniformBlock("DirectionalLight");
  }

  public static TerrainShader getInstance() {

    if (instance == null) {
      instance = new TerrainShader();
    }
    return instance;
  }

  @Override
  public void updateUniforms(Renderable object) {
    bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
    bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);

    setUniform("clipplane", ContextHolder.getContext().getConfig().getClipplane());
    setUniformi("isRefraction", ContextHolder.getContext().getConfig().isRenderRefraction() ? 1 : 0);
    setUniformi("isReflection", ContextHolder.getContext().getConfig().isRenderReflection() ? 1 : 0);
    setUniformi("isCameraUnderWater", ContextHolder.getContext().getConfig().isRenderUnderwater() ? 1 : 0);

    GLTerrainConfig terrConfig = object.getComponent(NodeComponentType.CONFIGURATION);
    ChunkConfig vChunkConfig = ((QuadtreeNode) object).getChunkConfig();

    int lod = vChunkConfig.getLod();
    Vec2f index = vChunkConfig.getIndex();
    float gap = vChunkConfig.getGap();
    Vec2f location = vChunkConfig.getLocation();

    setUniform("localMatrix", object.getLocalTransform().getWorldMatrix());
    setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());

    glActiveTexture(GL_TEXTURE0);
    terrConfig.getHeightmap().bind();
    setUniformi("heightmap", 0);

    glActiveTexture(GL_TEXTURE1);
    terrConfig.getNormalmap().bind();
    setUniformi("normalmap", 1);

    glActiveTexture(GL_TEXTURE2);
    terrConfig.getSplatmap().bind();
    setUniformi("splatmap", 2);

    setUniformi("lod", lod);
    setUniform("index", index);
    setUniformf("gap", gap);
    setUniform("location", location);

    setUniformf("yScale", terrConfig.getVerticalScaling());
    setUniformf("reflectionOffset", terrConfig.getReflectionOffset());

    glActiveTexture(GL_TEXTURE3);
    final GLOreonContext context = (GLOreonContext) ContextHolder.getContext();
    context.getResources().getUnderwaterCausticsMap().bind();
    setUniformi("caustics", 3);
    glActiveTexture(GL_TEXTURE4);
    context.getResources().getUnderwaterDudvMap().bind();
    setUniformi("dudvCaustics", 4);
    if (context.getResources().getWaterConfig() != null) {
      setUniformf("distortionCaustics", context.getResources().getWaterConfig().getDistortion());
      setUniformf("underwaterBlurFactor", context.getResources().getWaterConfig().getUnderwaterBlur());
    }

    int texUnit = 5;
    for (int i = 0; i < 3; i++) {

      glActiveTexture(GL_TEXTURE0 + texUnit);
      terrConfig.getMaterials().get(i).getDiffusemap().bind();
      setUniformi("materials[" + i + "].diffusemap", texUnit);
      texUnit++;

      glActiveTexture(GL_TEXTURE0 + texUnit);
      terrConfig.getMaterials().get(i).getHeightmap().bind();
      setUniformi("materials[" + i + "].heightmap", texUnit);
      texUnit++;

      glActiveTexture(GL_TEXTURE0 + texUnit);
      terrConfig.getMaterials().get(i).getNormalmap().bind();
      setUniformi("materials[" + i + "].normalmap", texUnit);
      texUnit++;

      setUniformf("materials[" + i + "].heightScaling", terrConfig.getMaterials().get(i).getHeightScaling());
      setUniformf("materials[" + i + "].uvScaling", terrConfig.getMaterials().get(i).getHorizontalScaling());
    }
  }
}
