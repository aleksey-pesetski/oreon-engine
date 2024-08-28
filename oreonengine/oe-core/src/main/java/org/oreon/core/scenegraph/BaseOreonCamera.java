package org.oreon.core.scenegraph;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import java.nio.FloatBuffer;
import lombok.Getter;
import org.oreon.core.CoreEngine;
import org.oreon.core.context.Config;
import org.oreon.core.context.OreonCamera;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.math.Vec4f;
import org.oreon.core.platform.Input;
import org.oreon.core.platform.Window;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.Util;

public abstract class BaseOreonCamera implements OreonCamera {

  private final Vec3f yAxis = new Vec3f(0, 1, 0);

  private final Config config;
  private final Input input;
  private final Window window;

  @Getter
  private Vec3f position;
  @Getter
  private Vec3f previousPosition;
  @Getter
  private Vec3f forward;
  @Getter
  private Vec3f previousForward;

  @Getter
  private Vec3f up;

  private float movAmt = 8.0f;
  private float rotAmt = 1.0f;

  @Getter
  private Matrix4f projectionMatrix;
  @Getter
  private Matrix4f viewMatrix;
  @Getter
  private Matrix4f viewProjectionMatrix;
  private Matrix4f originViewMatrix;
  @Getter
  private Matrix4f originViewProjectionMatrix;
  private Matrix4f xzOriginViewMatrix;
  @Getter
  private Matrix4f xzOriginViewProjectionMatrix;
  private Matrix4f previousViewMatrix;
  @Getter
  private Matrix4f previousViewProjectionMatrix;

  @Getter
  private boolean isCameraMoved;
  @Getter
  private boolean isCameraRotated;

  @Getter
  private float width;
  @Getter
  private float height;

  @Getter
  private float fovY;

  private float rotYstride;
  private float rotYamt = 0;
  private float rotXstride;
  private float rotXamt = 0;
  private float mouseSensitivity = 0.04f;

  private boolean isUpRotation;
  private boolean isDownRotation;
  private boolean isLeftRotation;
  private boolean isRightRotation;

  private Vec4f[] frustumPlanes = new Vec4f[6];
  private Vec3f[] frustumCorners = new Vec3f[8];

  protected FloatBuffer floatBuffer;
  protected final int bufferSize = Float.BYTES * (4 + 16 + 16 + (6 * 4) + (4));

  protected BaseOreonCamera(final Config config, final Input input, final Window window,
      final Vec3f position, final Vec3f forward, final Vec3f up) {
    this.config = config;
    this.input = input;
    this.window = window;

    this.width = config.getFrameWidth();
    this.height = config.getFrameHeight();

    this.previousViewMatrix = new Matrix4f().Zero();
    this.previousViewProjectionMatrix = new Matrix4f().Zero();
    this.previousPosition = new Vec3f(0, 0, 0);
    this.floatBuffer = BufferUtil.createFloatBuffer(bufferSize);

    this.position = position;
    this.forward = forward.normalize();
    this.up = up.normalize();
    setProjection(70, width, height);

    viewMatrix = new Matrix4f().View(forward, up)
        .mul(new Matrix4f().Translation(position.mul(-1)));
    originViewMatrix = new Matrix4f().View(forward, up)
        .mul(new Matrix4f().Identity());
    xzOriginViewMatrix = new Matrix4f().View(forward, up)
        .mul(new Matrix4f().Translation(new Vec3f(0, position.getY(), 0).mul(-1)));
    initfrustumPlanes();
    viewProjectionMatrix = projectionMatrix.mul(viewMatrix);
    originViewProjectionMatrix = projectionMatrix.mul(originViewMatrix);
  }

  @Override
  public void update() {
    previousPosition = new Vec3f(position);
    previousForward = new Vec3f(forward);

    isCameraMoved = false;
    isCameraRotated = false;

    movAmt = movAmt + (input.getScrollOffset() / 2);
    movAmt = Math.max(0.1f, movAmt);

    if (input.isKeyHolding(GLFW_KEY_W)) {
      move(forward, movAmt);
    }
    if (input.isKeyHolding(GLFW_KEY_S)) {
      move(forward, -movAmt);
    }
    if (input.isKeyHolding(GLFW_KEY_A)) {
      move(getLeft(), movAmt);
    }
    if (input.isKeyHolding(GLFW_KEY_D)) {
      move(getRight(), movAmt);
    }

    if (input.isKeyHolding(GLFW_KEY_UP)) {
      rotateX(-rotAmt / 8f);
    }
    if (input.isKeyHolding(GLFW_KEY_DOWN)) {
      rotateX(rotAmt / 8f);
    }
    if (input.isKeyHolding(GLFW_KEY_LEFT)) {
      rotateY(-rotAmt / 8f);
    }
    if (input.isKeyHolding(GLFW_KEY_RIGHT)) {
      rotateY(rotAmt / 8f);
    }

    // free mouse rotation
    if (input.isButtonHolding(0) || input.isButtonHolding(2)) {
      float dy = input.getLockedCursorPosition().getY() - input.getCursorPosition().getY();
      float dx = input.getLockedCursorPosition().getX() - input.getCursorPosition().getX();

      if (Math.abs(dy) < 1) {
        dy = 0;
      }
      if (Math.abs(dx) < 1) {
        dx = 0;
      }

      // y-axxis rotation
      if (dy != 0) {
        rotYamt = rotYamt - dy;
        rotYstride = Math.abs(rotYamt * CoreEngine.currentFrameTime * 10);
      }

      if (rotYamt != 0 || rotYstride != 0) {
        // up-rotation
        if (rotYamt < 0) {
          isUpRotation = true;
          isDownRotation = false;
          rotateX(-rotYstride * mouseSensitivity);
          rotYamt = rotYamt + rotYstride;
          if (rotYamt > 0) {
            rotYamt = 0;
          }
        }
        // down-rotation
        if (rotYamt > 0) {
          isUpRotation = false;
          isDownRotation = true;
          rotateX(rotYstride * mouseSensitivity);
          rotYamt = rotYamt - rotYstride;
          if (rotYamt < 0) {
            rotYamt = 0;
          }
        }

        // smooth-stop
        if (rotYamt == 0) {
          rotYstride = rotYstride * 0.85f;
          if (isUpRotation) {
            rotateX(-rotYstride * mouseSensitivity);
          }
          if (isDownRotation) {
            rotateX(rotYstride * mouseSensitivity);
          }
          if (rotYstride < 0.001f) {
            rotYstride = 0;
          }
        }
      }

      // x-axxis rotation
      if (dx != 0) {
        rotXamt = rotXamt + dx;
        rotXstride = Math.abs(rotXamt * CoreEngine.currentFrameTime * 10);
      }

      if (rotXamt != 0 || rotXstride != 0) {
        // right-rotation
        if (rotXamt < 0) {
          isRightRotation = true;
          isLeftRotation = false;
          rotateY(rotXstride * mouseSensitivity);
          rotXamt = rotXamt + rotXstride;
          if (rotXamt > 0) {
            rotXamt = 0;
          }
        }
        // left-rotation
        if (rotXamt > 0) {
          isRightRotation = false;
          isLeftRotation = true;
          rotateY(-rotXstride * mouseSensitivity);
          rotXamt = rotXamt - rotXstride;
          if (rotXamt < 0) {
            rotXamt = 0;
          }
        }
        // smooth-stop
        if (rotXamt == 0) {
          rotXstride = rotXstride * 0.85f;
          if (isRightRotation) {
            rotateY(rotXstride * mouseSensitivity);
          }
          if (isLeftRotation) {
            rotateY(-rotXstride * mouseSensitivity);
          }
          if (rotXstride < 0.001f) {
            rotXstride = 0;
          }
        }
      }

      glfwSetCursorPos(
          window.getId(),
          input.getLockedCursorPosition().getX(),
          input.getLockedCursorPosition().getY()
      );
    }

    if (!position.equals(previousPosition)) {
      isCameraMoved = true;
    }

    if (!forward.equals(previousForward)) {
      isCameraRotated = true;
    }

    previousViewMatrix = viewMatrix;
    previousViewProjectionMatrix = viewProjectionMatrix;
    Matrix4f vOriginViewMatrix = new Matrix4f().View(forward, up);
    viewMatrix = vOriginViewMatrix.mul(new Matrix4f().Translation(position.mul(-1)));
    originViewMatrix = vOriginViewMatrix;
    xzOriginViewMatrix = vOriginViewMatrix.mul(new Matrix4f().Translation(new Vec3f(0, position.getY(), 0).mul(-1)));
    viewProjectionMatrix = projectionMatrix.mul(viewMatrix);
    originViewProjectionMatrix = projectionMatrix.mul(originViewMatrix);
    xzOriginViewProjectionMatrix = projectionMatrix.mul(xzOriginViewMatrix);

    floatBuffer.clear();
    floatBuffer.put(BufferUtil.createFlippedBuffer(position));
    floatBuffer.put(0);
    floatBuffer.put(BufferUtil.createFlippedBuffer(viewMatrix));
    floatBuffer.put(BufferUtil.createFlippedBuffer(viewProjectionMatrix));
    floatBuffer.put(BufferUtil.createFlippedBuffer(frustumPlanes));
    floatBuffer.put(width);
    floatBuffer.put(height);
    floatBuffer.put(0);
    floatBuffer.put(0);
    floatBuffer.flip();
  }

  public void move(Vec3f dir, float amount) {
    position = position.add(dir.mul(amount));
  }

  /**
   * ax * bx * cx +  d = 0; store a,b,c,d
   */
  private void initfrustumPlanes() {
    //left plane
    Vec4f leftPlane = new Vec4f(
        this.projectionMatrix.get(3, 0) + this.projectionMatrix.get(0, 0)
            * (float) ((Math.tan(Math.toRadians(this.fovY / 2))
            * ((double) config.getFrameWidth()
            / (double) config.getFrameHeight()))),
        this.projectionMatrix.get(3, 1) + this.projectionMatrix.get(0, 1),
        this.projectionMatrix.get(3, 2) + this.projectionMatrix.get(0, 2),
        this.projectionMatrix.get(3, 3) + this.projectionMatrix.get(0, 3));

    this.frustumPlanes[0] = Util.normalizePlane(leftPlane);

    //right plane
    Vec4f rightPlane = new Vec4f(
        this.projectionMatrix.get(3, 0) - this.projectionMatrix.get(0, 0)
            * (float) ((Math.tan(Math.toRadians(this.fovY / 2))
            * ((double) config.getFrameWidth()
            / (double) config.getFrameHeight()))),
        this.projectionMatrix.get(3, 1) - this.projectionMatrix.get(0, 1),
        this.projectionMatrix.get(3, 2) - this.projectionMatrix.get(0, 2),
        this.projectionMatrix.get(3, 3) - this.projectionMatrix.get(0, 3));

    this.frustumPlanes[1] = Util.normalizePlane(rightPlane);

    //bot plane
    Vec4f botPlane = new Vec4f(
        this.projectionMatrix.get(3, 0) + this.projectionMatrix.get(1, 0),
        this.projectionMatrix.get(3, 1) + this.projectionMatrix.get(1, 1)
            * (float) Math.tan(Math.toRadians(this.fovY / 2)),
        this.projectionMatrix.get(3, 2) + this.projectionMatrix.get(1, 2),
        this.projectionMatrix.get(3, 3) + this.projectionMatrix.get(1, 3));

    this.frustumPlanes[2] = Util.normalizePlane(botPlane);

    //top plane
    Vec4f topPlane = new Vec4f(
        this.projectionMatrix.get(3, 0) - this.projectionMatrix.get(1, 0),
        this.projectionMatrix.get(3, 1) - this.projectionMatrix.get(1, 1)
            * (float) Math.tan(Math.toRadians(this.fovY / 2)),
        this.projectionMatrix.get(3, 2) - this.projectionMatrix.get(1, 2),
        this.projectionMatrix.get(3, 3) - this.projectionMatrix.get(1, 3));

    this.frustumPlanes[3] = Util.normalizePlane(topPlane);

    //near plane
    Vec4f nearPlane = new Vec4f(
        this.projectionMatrix.get(3, 0) + this.projectionMatrix.get(2, 0),
        this.projectionMatrix.get(3, 1) + this.projectionMatrix.get(2, 1),
        this.projectionMatrix.get(3, 2) + this.projectionMatrix.get(2, 2),
        this.projectionMatrix.get(3, 3) + this.projectionMatrix.get(2, 3));

    this.frustumPlanes[4] = Util.normalizePlane(nearPlane);

    //far plane
    Vec4f farPlane = new Vec4f(
        this.projectionMatrix.get(3, 0) - this.projectionMatrix.get(2, 0),
        this.projectionMatrix.get(3, 1) - this.projectionMatrix.get(2, 1),
        this.projectionMatrix.get(3, 2) - this.projectionMatrix.get(2, 2),
        this.projectionMatrix.get(3, 3) - this.projectionMatrix.get(2, 3));

    this.frustumPlanes[5] = Util.normalizePlane(farPlane);
  }

  public void rotateY(float angle) {
    Vec3f hAxis = yAxis.cross(forward).normalize();

    forward.rotate(angle, yAxis).normalize();

    up = forward.cross(hAxis).normalize();

    // this is for align y-axxis of camera vectors
    // there is a kind of numeric bug, when camera is rotating very fast, camera vectors skewing
    hAxis = yAxis.cross(forward).normalize();
    forward.rotate(0, yAxis).normalize();
    up = forward.cross(hAxis).normalize();
  }

  public void rotateX(float angle) {
    Vec3f hAxis = yAxis.cross(forward).normalize();

    forward.rotate(angle, hAxis).normalize();

    up = forward.cross(hAxis).normalize();
  }

  public Vec3f getLeft() {
    Vec3f left = forward.cross(up);
    left.normalize();
    return left;
  }

  public Vec3f getRight() {
    Vec3f right = up.cross(forward);
    right.normalize();
    return right;
  }

  public void setProjection(float fovY, float width, float height) {
    this.fovY = fovY;
    this.width = width;
    this.height = height;

    this.projectionMatrix = new Matrix4f().PerspectiveProjection(
        fovY, width, height, Constants.ZNEAR, Constants.ZFAR);
  }
}