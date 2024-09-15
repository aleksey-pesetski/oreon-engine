package org.oreon.core.platform;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.oreon.core.util.ResourceLoaderUtils;

public abstract class Window {

  private long id;
  private int width;
  private int height;
  private String title;

  public abstract void create();

  public abstract void show();

  public abstract void draw();

  public abstract void shutdown();

  public abstract boolean isCloseRequested();

  public abstract void resize(int x, int y);

  public Window(String title, int width, int height) {
    this.width = width;
    this.height = height;
    this.title = title;
  }

  public void setIcon(String path) {
    final ByteBuffer bufferedImage = ResourceLoaderUtils.loadImageToByteBuffer(path);

    var w = BufferUtils.createIntBuffer(1);
    var h = BufferUtils.createIntBuffer(1);
    var c = BufferUtils.createIntBuffer(1);

    // Decode the image
    ByteBuffer imageBuffer = stbi_load_from_memory(bufferedImage, w, h, c, 0);
    if (imageBuffer == null) {
      throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
    }

    GLFWImage image = GLFWImage.malloc();
    image.set(32, 32, imageBuffer);

    GLFWImage.Buffer images = GLFWImage.malloc(1);
    images.put(0, image);

    glfwSetWindowIcon(getId(), images);
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public long getId() {
    return id;
  }

  public void setId(long window) {
    this.id = window;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
