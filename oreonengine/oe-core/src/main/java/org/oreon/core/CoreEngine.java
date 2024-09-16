package org.oreon.core;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.oreon.core.platform.Input;
import org.oreon.core.platform.Window;
import org.oreon.core.util.Constants;

@Log4j2
public class CoreEngine {

  public static float currentFrameTime = 0;
  @Getter
  @Setter
  private static int fps;
  private static float framerate = 1000;
  @Getter
  private static float frameTime = 1.0f / framerate;
  private boolean isRunning;

  private Window window;
  private Input input;
  private BaseOreonRenderEngine renderEngine;
  private GLFWErrorCallback errorCallback;

  public CoreEngine(final Window window, final Input input, final BaseOreonRenderEngine renderEngine) {
    this.window = window;
    this.input = input;
    this.renderEngine = renderEngine;
  }

  private void init() {
    var logErr = IoBuilder.forLogger(log)
        .setLevel(Level.ERROR)
        .buildPrintStream();

    errorCallback = GLFWErrorCallback.createPrint(logErr);
    glfwSetErrorCallback(errorCallback);

    input.create(window.getId());
    window.show();
  }

  public void start() {
    init();

    if (isRunning) {
      return;
    }

    run();
  }

  public void run() {

    this.isRunning = true;

    int frames = 0;
    long frameCounter = 0;

    long lastTime = System.nanoTime();
    double unprocessedTime = 0;

    // Rendering Loop
    while (isRunning) {
      boolean render = false;

      long startTime = System.nanoTime();
      long passedTime = startTime - lastTime;
      lastTime = startTime;

      unprocessedTime += passedTime / (double) Constants.NANOSECOND;
      frameCounter += passedTime;

      while (unprocessedTime > frameTime) {
        render = true;
        unprocessedTime -= frameTime;

        if (window.isCloseRequested()) {
          stop();
        }

        if (frameCounter >= Constants.NANOSECOND) {
          setFps(frames);
          currentFrameTime = 1.0f / fps;
          frames = 0;
          frameCounter = 0;
        }
      }
      if (render) {
        update();
        render();
        frames++;
      } else {
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
      }
    }

    shutdown();
  }

  private void stop() {
    if (!isRunning) {
      return;
    }

    isRunning = false;
  }

  private void render() {
    renderEngine.render();
    window.draw();
  }

  private void update() {
    input.update();
    renderEngine.update();
  }

  private void shutdown() {
    window.shutdown();
    input.shutdown();
    renderEngine.shutdown();
    errorCallback.free();
    glfwTerminate();
  }
}
