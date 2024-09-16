package org.oreon.core.util;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.oreon.core.model.VertexLayout.POS2D;
import static org.oreon.core.model.VertexLayout.POS2D_UV;
import static org.oreon.core.model.VertexLayout.POS_NORMAL;
import static org.oreon.core.model.VertexLayout.POS_NORMAL_UV;
import static org.oreon.core.model.VertexLayout.POS_NORMAL_UV_TAN_BITAN;
import static org.oreon.core.model.VertexLayout.POS_UV;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.math.Vec4f;
import org.oreon.core.model.Vertex;
import org.oreon.core.model.VertexLayout;

public class BufferUtil {

  private static final Logger log = LogManager.getLogger(BufferUtil.class);

  private BufferUtil() {
  }

  public static FloatBuffer createFloatBuffer(int size) {
    return BufferUtils.createFloatBuffer(size);
  }

  public static IntBuffer createIntBuffer(int size) {
    return BufferUtils.createIntBuffer(size);
  }

  public static DoubleBuffer createDoubleBuffer(int size) {
    return BufferUtils.createDoubleBuffer(size);
  }

  public static IntBuffer createFlippedBuffer(int... values) {
    final IntBuffer buffer = createIntBuffer(values.length);
    buffer.put(values);
    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(float... values) {
    final FloatBuffer buffer = createFloatBuffer(values.length);
    buffer.put(values);
    buffer.flip();

    return buffer;
  }

  public static DoubleBuffer createFlippedBuffer(double... values) {
    final DoubleBuffer buffer = createDoubleBuffer(values.length);
    buffer.put(values);
    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBufferAOS(Vertex[] vertices) {
    final FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.FLOATS);

    for (Vertex vertex : vertices) {
      buffer.put(vertex.getPosition().getX());
      buffer.put(vertex.getPosition().getY());
      buffer.put(vertex.getPosition().getZ());

      buffer.put(vertex.getNormal().getX());
      buffer.put(vertex.getNormal().getY());
      buffer.put(vertex.getNormal().getZ());

      buffer.put(vertex.getUVCoord().getX());
      buffer.put(vertex.getUVCoord().getY());

      if (vertex.getTangent() != null && vertex.getBitangent() != null) {
        buffer.put(vertex.getTangent().getX());
        buffer.put(vertex.getTangent().getY());
        buffer.put(vertex.getTangent().getZ());

        buffer.put(vertex.getBitangent().getX());
        buffer.put(vertex.getBitangent().getY());
        buffer.put(vertex.getBitangent().getZ());
      }
    }

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBufferSOA(Vertex[] vertices) {
    final FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.FLOATS);

    for (Vertex vertex : vertices) {
      buffer.put(vertex.getPosition().getX());
      buffer.put(vertex.getPosition().getY());
      buffer.put(vertex.getPosition().getZ());

      buffer.put(vertex.getNormal().getX());
      buffer.put(vertex.getNormal().getY());
      buffer.put(vertex.getNormal().getZ());

      buffer.put(vertex.getUVCoord().getX());
      buffer.put(vertex.getUVCoord().getY());
    }

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Vec3f[] vector) {
    final FloatBuffer buffer = createFloatBuffer(vector.length * Float.BYTES * 3);

    for (Vec3f vec3f : vector) {
      buffer.put(vec3f.getX());
      buffer.put(vec3f.getY());
      buffer.put(vec3f.getZ());
    }

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Vec4f[] vector) {
    final FloatBuffer buffer = createFloatBuffer(vector.length * Float.BYTES * 4);

    for (Vec4f vec4f : vector) {
      buffer.put(vec4f.getX());
      buffer.put(vec4f.getY());
      buffer.put(vec4f.getZ());
      buffer.put(vec4f.getW());
    }

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Vec3f vector) {
    final FloatBuffer buffer = createFloatBuffer(Float.BYTES * 3);

    buffer.put(vector.getX());
    buffer.put(vector.getY());
    buffer.put(vector.getZ());

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Vec2f vector) {
    final FloatBuffer buffer = createFloatBuffer(Float.BYTES * 2);

    buffer.put(vector.getX());
    buffer.put(vector.getY());

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Vec4f vector) {
    final FloatBuffer buffer = createFloatBuffer(Float.BYTES * 4);

    buffer.put(vector.getX());
    buffer.put(vector.getY());
    buffer.put(vector.getZ());
    buffer.put(vector.getW());

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Vec2f[] vector) {
    final FloatBuffer buffer = createFloatBuffer(vector.length * Float.BYTES * 2);

    for (Vec2f vec2f : vector) {
      buffer.put(vec2f.getX());
      buffer.put(vec2f.getY());
    }

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(List<Vec2f> vector) {
    final FloatBuffer buffer = createFloatBuffer(vector.size() * Float.BYTES * 2);

    for (Vec2f v : vector) {
      buffer.put(v.getX());
      buffer.put(v.getY());
    }

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Matrix4f matrix) {
    final FloatBuffer buffer = createFloatBuffer(4 * 4);

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        buffer.put(matrix.get(i, j));
      }
    }

    buffer.flip();

    return buffer;
  }

  public static FloatBuffer createFlippedBuffer(Matrix4f[] matrices) {
    FloatBuffer buffer = createFloatBuffer(4 * 4 * matrices.length);

    for (Matrix4f matrix : matrices) {
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
          buffer.put(matrix.get(i, j));
        }
      }
    }

    buffer.flip();

    return buffer;
  }

  public static ByteBuffer createByteBuffer(Matrix4f matrix) {
    final ByteBuffer byteBuffer = memAlloc(Float.BYTES * 16);
    final FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    floatBuffer.put(BufferUtil.createFlippedBuffer(matrix));

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(Vec3f vector) {
    final ByteBuffer byteBuffer = memAlloc(Float.BYTES * 3);
    final FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    floatBuffer.put(BufferUtil.createFlippedBuffer(vector));

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(Vec2f[] vertices) {
    ByteBuffer byteBuffer = memAlloc(Float.BYTES * 2 * vertices.length);
    FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

    for (Vec2f vertex : vertices) {
      floatBuffer.put(vertex.getX());
      floatBuffer.put(vertex.getY());
    }

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(Vec3f[] vertices) {
    final ByteBuffer byteBuffer = memAlloc(Float.BYTES * 3 * vertices.length);
    final FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

    for (Vec3f vertex : vertices) {
      floatBuffer.put(vertex.getX());
      floatBuffer.put(vertex.getY());
      floatBuffer.put(vertex.getZ());
    }

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(Vec4f[] vertices) {
    final ByteBuffer byteBuffer = memAlloc(Float.BYTES * 4 * vertices.length);
    final FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

    for (Vec4f vertex : vertices) {
      floatBuffer.put(vertex.getX());
      floatBuffer.put(vertex.getY());
      floatBuffer.put(vertex.getZ());
      floatBuffer.put(vertex.getW());
    }

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(Vertex[] vertices, VertexLayout layout) {
    final ByteBuffer byteBuffer = allocateVertexByteBuffer(layout, vertices.length);
    final FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

    for (Vertex vertex : vertices) {
      if (layout == POS2D || layout == POS2D_UV) {
        floatBuffer.put(vertex.getPosition().getX());
        floatBuffer.put(vertex.getPosition().getY());
      } else {
        floatBuffer.put(vertex.getPosition().getX());
        floatBuffer.put(vertex.getPosition().getY());
        floatBuffer.put(vertex.getPosition().getZ());
      }

      if (layout == POS_NORMAL || layout == POS_NORMAL_UV || layout == POS_NORMAL_UV_TAN_BITAN) {
        floatBuffer.put(vertex.getNormal().getX());
        floatBuffer.put(vertex.getNormal().getY());
        floatBuffer.put(vertex.getNormal().getZ());
      }

      if (layout == POS_NORMAL_UV || layout == POS_UV || layout == POS_NORMAL_UV_TAN_BITAN || layout == POS2D_UV) {
        floatBuffer.put(vertex.getUVCoord().getX());
        floatBuffer.put(vertex.getUVCoord().getY());
      }

      if (layout == POS_NORMAL_UV_TAN_BITAN) {
        floatBuffer.put(vertex.getTangent().getX());
        floatBuffer.put(vertex.getTangent().getY());
        floatBuffer.put(vertex.getTangent().getZ());

        floatBuffer.put(vertex.getBitangent().getX());
        floatBuffer.put(vertex.getBitangent().getY());
        floatBuffer.put(vertex.getBitangent().getZ());
      }
    }

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(int... values) {
    final ByteBuffer byteBuffer = memAlloc(Integer.BYTES * values.length);
    final IntBuffer intBuffer = byteBuffer.asIntBuffer();
    intBuffer.put(values);
    intBuffer.flip();

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(float... values) {
    final ByteBuffer byteBuffer = memAlloc(Float.BYTES * values.length);
    final FloatBuffer intBuffer = byteBuffer.asFloatBuffer();
    intBuffer.put(values);
    intBuffer.flip();

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(FloatBuffer floatBuffer) {
    final ByteBuffer byteBuffer = memAlloc(Float.BYTES * floatBuffer.limit());
    final FloatBuffer intBuffer = byteBuffer.asFloatBuffer();
    intBuffer.put(floatBuffer);
    intBuffer.flip();

    return byteBuffer;
  }

  public static ByteBuffer createByteBuffer(Vec2f vector) {
    return createByteBuffer(createFlippedBuffer(vector));
  }

  public static ByteBuffer allocateVertexByteBuffer(VertexLayout layout, int vertexCount) {
    return switch (layout) {
      case POS2D -> memAlloc(Float.BYTES * 2 * vertexCount);
      case POS -> memAlloc(Float.BYTES * 3 * vertexCount);
      case POS_UV -> memAlloc(Float.BYTES * 5 * vertexCount);
      case POS2D_UV -> memAlloc(Float.BYTES * 4 * vertexCount);
      case POS_NORMAL -> memAlloc(Float.BYTES * 6 * vertexCount);
      case POS_NORMAL_UV -> memAlloc(Float.BYTES * 8 * vertexCount);
      case POS_NORMAL_UV_TAN_BITAN -> memAlloc(Float.BYTES * 14 * vertexCount);
      default -> memAlloc(0);
    };
  }

  public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    final ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }

}
