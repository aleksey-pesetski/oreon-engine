package org.oreon.core.gl.memory;

import org.oreon.core.math.Vec3f;

/**
 * Vertex Array Object
 */

public interface VBO {

  void draw();

  void update(Vec3f[] vertices);

  void delete();
}
