package org.oreon.core.gl.context;

import static org.lwjgl.glfw.GLFW.glfwInit;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.opengl.GL11;
import org.oreon.core.BaseOreonRenderEngine;
import org.oreon.core.CoreEngine;
import org.oreon.core.context.BaseOreonContext;
import org.oreon.core.context.Config;
import org.oreon.core.gl.platform.GLWindow;
import org.oreon.core.gl.scenegraph.GLCamera;
import org.oreon.core.gl.util.GLUtil;
import org.oreon.core.platform.GLFWInput;

@Log4j2
@Getter
public class GLOreonContext extends BaseOreonContext<GLFWInput, GLCamera, GLWindow, GLResources> {

  public GLOreonContext(
      final Config config,
      final GLFWInput input,
      final GLResources resources,
      final GLWindow window,
      final GLCamera camera,
      final BaseOreonRenderEngine renderEngine,
      final CoreEngine coreEngine) {
    super(input, camera, window, resources, config, renderEngine, coreEngine);

    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    // create OpenGL Context
    getWindow().create();

    log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
//		log.info("Max Geometry Uniform Blocks: " + GL11.glGetInteger(GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS));
//		log.info("Max Geometry Shader Invocations: " + GL11.glGetInteger(GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS));
//		log.info("Max Uniform Buffer Bindings: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS));
//		log.info("Max Uniform Block Size: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE) + " bytes");
//		log.info("Max SSBO Block Size: " + GL11.glGetInteger(GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE) + " bytes");
//		log.info("Max Image Bindings: " + GL11.glGetInteger(GL42.GL_MAX_IMAGE_UNITS));

    GLUtil.init();
  }
}
