package org.oreon.core.gl.surface;

import lombok.Getter;
import lombok.Setter;
import org.oreon.core.gl.memory.GLMeshVBO;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.pipeline.RenderParameter;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.parameter.DefaultRenderParams;
import org.oreon.core.math.Vec2f;
import org.oreon.core.util.MeshGenerator;

@Getter
public class FullScreenMultisampleQuad {

  @Setter
  private GLTexture texture;
  @Setter
  private GLShaderProgram shader;
  @Setter
  private GLMeshVBO vao;
  @Setter
  private RenderParameter config;
  private Vec2f[] texCoords;

  public FullScreenMultisampleQuad() {
    shader = FullScreenMSQuadShader.getInstance();
    config = new DefaultRenderParams();
    vao = new GLMeshVBO();
    vao.addData(MeshGenerator.NDCQuad2D());
  }


  public void render() {
    getConfig().enable();
    getShader().bind();
    getShader().updateUniforms(texture);
    getVao().draw();
    getConfig().disable();
  }

}
