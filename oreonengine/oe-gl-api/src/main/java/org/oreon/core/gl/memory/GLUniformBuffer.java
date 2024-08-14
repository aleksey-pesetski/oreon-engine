package org.oreon.core.gl.memory;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glMapBuffer;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import lombok.Getter;
import lombok.Setter;

/**
 * Uniform Buffer Object
 */

public class GLUniformBuffer {

  private final int ubo;
  @Setter
  @Getter
  private int bindingPointIndex;
  @Setter
  @Getter
  private String bindingName;

  public GLUniformBuffer() {
    ubo = glGenBuffers();
    bindingName = "";
  }

  public void allocate(int bytes) {
    glBindBuffer(GL_UNIFORM_BUFFER, ubo);
    glBufferData(GL_UNIFORM_BUFFER, bytes, GL_DYNAMIC_DRAW);
  }

  public void addData(FloatBuffer buffer) {
    glBindBuffer(GL_UNIFORM_BUFFER, ubo);
    glBufferData(GL_UNIFORM_BUFFER, buffer, GL_DYNAMIC_DRAW);
  }

  public void updateData(FloatBuffer buffer, int length) {
    glBindBuffer(GL_UNIFORM_BUFFER, ubo);
    ByteBuffer mappedBuffer = glMapBuffer(GL_UNIFORM_BUFFER, GL_READ_WRITE, length, null);
    mappedBuffer.clear();
    for (int i = 0; i < length / Float.BYTES; i++) {
      mappedBuffer.putFloat(buffer.get(i));
    }
    mappedBuffer.flip();
    glUnmapBuffer(GL_UNIFORM_BUFFER);
  }

  public void addData(ByteBuffer buffer) {
    glBindBuffer(GL_UNIFORM_BUFFER, ubo);
    glBufferData(GL_UNIFORM_BUFFER, buffer, GL_DYNAMIC_DRAW);
  }

  public void updateData(ByteBuffer buffer, int length) {
    glBindBuffer(GL_UNIFORM_BUFFER, ubo);
    ByteBuffer mappedBuffer = glMapBuffer(GL_UNIFORM_BUFFER, GL_READ_WRITE, length, null);
    mappedBuffer.clear();
    for (int i = 0; i < length / Float.BYTES; i++) {
      mappedBuffer.putFloat(buffer.get(i));
    }
    mappedBuffer.flip();
    glUnmapBuffer(GL_UNIFORM_BUFFER);
  }

  public void bind() {
    glBindBuffer(GL_UNIFORM_BUFFER, ubo);
  }

  public void bindBufferBase() {
    glBindBufferBase(GL_UNIFORM_BUFFER, bindingPointIndex, ubo);
  }

  public void bindBufferBase(int index) {
    glBindBufferBase(GL_UNIFORM_BUFFER, index, ubo);
  }

}
