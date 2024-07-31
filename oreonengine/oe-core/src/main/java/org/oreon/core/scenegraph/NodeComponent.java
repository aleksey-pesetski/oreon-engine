package org.oreon.core.scenegraph;

import lombok.Data;
import org.oreon.core.math.Transform;

@Data
public abstract class NodeComponent implements Cloneable {

  private Renderable parent;

  public void update() {
  }

  public void input() {
  }

  public void render() {
  }

  public void shutdown() {
  }

  public Transform getTransform() {
    return parent.getWorldTransform();
  }

  public NodeComponent clone() throws CloneNotSupportedException {
    return (NodeComponent) super.clone();
  }
}