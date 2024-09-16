package org.oreon.core.gl.framebuffer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_UNDEFINED;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_UNSUPPORTED;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL30.glRenderbufferStorageMultisample;
import static org.lwjgl.opengl.GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

import java.nio.IntBuffer;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;


@Getter
@Log4j2
public class GLFramebuffer {

  private final int id;

  public GLFramebuffer() {
    id = glGenFramebuffers();
  }

  public void bind() {
    glBindFramebuffer(GL_FRAMEBUFFER, id);
  }

  public void unbind() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
  }

  public void setDrawBuffer(int i) {
    glDrawBuffer(GL_COLOR_ATTACHMENT0 + i);
  }

  public void setDrawBuffers(IntBuffer buffer) {
    glDrawBuffers(buffer);
  }

  public void createColorBufferAttachment(int x, int y, int i, int internalformat) {
    final int colorbuffer = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, colorbuffer);
    glRenderbufferStorage(GL_RENDERBUFFER, internalformat, x, y);
    glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_RENDERBUFFER, colorbuffer);
  }

  public void createColorTextureAttachment(int texture, int i) {
    glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, texture, 0);
  }

  public void createColorTextureAttachment(int texture, int i, boolean isMultisample) {
    glFramebufferTexture2D(
        GL_DRAW_FRAMEBUFFER,
        GL_COLOR_ATTACHMENT0 + i,
        isMultisample
            ? GL_TEXTURE_2D_MULTISAMPLE
            : GL_TEXTURE_2D,
        texture,
        0);
  }

  public void createDepthBufferAttachment(int x, int y) {
    int depthBuffer = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32F, x, y);
    glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
  }

  public void createDepthTextureAttachment(int texture) {
    glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, texture, 0);
  }

  public void createColorTextureMultisampleAttachment(int texture, int i) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D_MULTISAMPLE, texture, 0);
  }

  public void createDepthTextureMultisampleAttachment(int texture) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE, texture, 0);
  }

  public void createDepthTextureAttachment(int texture, boolean isMultisample) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
        isMultisample ? GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D, texture, 0);
  }

  public void createColorBufferMultisampleAttachment(int samples, int attachment, int width, int height,
      int internalformat) {
    int colorBuffer = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, colorBuffer);
    glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, internalformat, width, height);
    glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + attachment,
        GL_RENDERBUFFER, colorBuffer);
  }

  public void createDepthBufferMultisampleAttachment(int samples, int width, int height) {
    int depthBuffer = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
    glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT32F, width, height);
    glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
        GL_RENDERBUFFER, depthBuffer);
  }

  public void blitFrameBuffer(int sourceAttachment, int destinationAttachment, int writeFBO, int width, int height) {
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, writeFBO);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
    glReadBuffer(GL_COLOR_ATTACHMENT0 + sourceAttachment);
    glDrawBuffer(GL_COLOR_ATTACHMENT0 + destinationAttachment);
    glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
        GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
  }

  public void checkStatus() {
    int framebufferStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);

    switch (framebufferStatus) {
      case GL_FRAMEBUFFER_COMPLETE -> log.info("Framebuffer creation successful with GL_FRAMEBUFFER_COMPLETE status");
      case GL_FRAMEBUFFER_UNDEFINED -> logAndExit("GL_FRAMEBUFFER_UNDEFINED");
      case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> logAndExit("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
      case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> logAndExit("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
      case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> logAndExit("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
      case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> logAndExit("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
      case GL_FRAMEBUFFER_UNSUPPORTED -> logAndExit("GL_FRAMEBUFFER_UNSUPPORTED");
      case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE -> logAndExit("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
      case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS -> logAndExit("GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS");
      default -> logAndExit("unknown error: " + framebufferStatus);
    }
  }

  private void logAndExit(String error) {
    log.error("Framebuffer creation failed with {} error", error);
    System.exit(1);
  }

}
