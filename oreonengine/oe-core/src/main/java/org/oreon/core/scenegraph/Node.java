package org.oreon.core.scenegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.oreon.core.math.Transform;

@Data
public class Node {

  private final String id;

  private Node parent;
  private List<Node> children;
  private Transform worldTransform;
  private Transform localTransform;

  public Node() {
    this.id = UUID.randomUUID().toString();
    this.children = new ArrayList<>();

    setWorldTransform(new Transform());
    setLocalTransform(new Transform());
    setChildren(new ArrayList<>());
  }

  public void addChild(Node child) {
    child.setParent(this);
    this.children.add(child);
  }

  public void update() {
    getWorldTransform().setRotation(
        getWorldTransform().getLocalRotation().add(getParentNode().getWorldTransform().getRotation()));
    getWorldTransform().setTranslation(
        getWorldTransform().getLocalTranslation().add(getParentNode().getWorldTransform().getTranslation()));
    getWorldTransform().setScaling(
        getWorldTransform().getLocalScaling().mul(getParentNode().getWorldTransform().getScaling()));

    for (Node child : this.children) {
      child.update();
    }
  }

  public void updateLights() {
    for (Node child : this.children) {
      child.updateLights();
    }
  }

  public void input() {
    this.children.forEach(Node::input);
  }

  public void render() {
    this.children.forEach(Node::render);
  }

  public void renderWireframe() {
    this.children.forEach(Node::renderWireframe);
  }

  public void renderShadows() {
    this.children.forEach(Node::renderShadows);
  }

  public void record(RenderList renderList) {
    this.children.forEach(child -> child.record(renderList));
  }

  public void shutdown() {
    this.children.forEach(Node::shutdown);
  }

  public Node getParentNode() {
    return this.parent;
  }

  @SuppressWarnings("unchecked")
  public <T> T getParentObject() {
    return (T) this.parent;
  }
}
