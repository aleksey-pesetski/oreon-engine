package org.oreon.common.quadtree;

import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.scenegraph.NodeComponentType;

@Log4j2
public abstract class Quadtree extends Node implements Runnable {

  private final Thread thread;
  private final Lock startUpdateQuadtreeLock;
  private final Condition startUpdateQuadtreeCondition;

  private boolean isRunning;
  private int updateCounter;

  @Getter
  protected QuadtreeCache quadtreeCache;

  public Quadtree() {
    isRunning = false;
    startUpdateQuadtreeLock = new ReentrantLock();
    startUpdateQuadtreeCondition = startUpdateQuadtreeLock.newCondition();
    thread = new Thread(this);
    quadtreeCache = new QuadtreeCache();
  }

  public void updateQuadtree() {
    if (ContextHolder.getContext().getCamera().isCameraMoved()) {
      updateCounter++;
    }

    if (updateCounter == 2) {
      for (Node node : getChildren()) {
        log.debug("Updating quadtree: id {}.", node.getId());
        ((QuadtreeNode) node).updateQuadtree();
      }
      updateCounter = 0;
    }
  }

  public void start() {
    thread.start();
  }

  @Override
  public void run() {
    isRunning = true;

    while (isRunning) {
      startUpdateQuadtreeLock.lock();
      try {
        startUpdateQuadtreeCondition.await();
      } catch (InterruptedException e) {
        log.error("Interrupted", e);
        thread.interrupt();
      } finally {
        startUpdateQuadtreeLock.unlock();
      }

      updateQuadtree();
    }
  }

  public void signal() {
    startUpdateQuadtreeLock.lock();
    try {
      startUpdateQuadtreeCondition.signal();
    } finally {
      startUpdateQuadtreeLock.unlock();
    }
  }

  @Override
  public void shutdown() {
    isRunning = false;
    thread.interrupt();
  }

  @Override
  public void update() {
  }

  public abstract QuadtreeNode createChildChunk(Map<NodeComponentType, NodeComponent> components,
      QuadtreeCache quadtreeCache, Transform worldTransform,
      Vec2f location, int levelOfDetail, Vec2f index);

}
