package org.oreon.core.scenegraph;

import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class Renderable extends Node {

  @Getter
  private final Map<NodeComponentType, NodeComponent> components;

  @Setter
  protected boolean render;

  public Renderable() {
    super();

    render = true;
    components = new EnumMap<>(NodeComponentType.class);
  }

  public void addComponent(NodeComponentType type, NodeComponent component) {
    component.setParent(this);
    components.put(type, component);
  }

  @Override
  public void update() {
    for (Map.Entry<NodeComponentType, NodeComponent> entry : components.entrySet()) {
      if (entry.getKey() != NodeComponentType.LIGHT) {
        entry.getValue().update();
      }
    }

    super.update();
  }

  @Override
  public void updateLights() {
    for (Map.Entry<NodeComponentType, NodeComponent> entry : components.entrySet()) {
      if (entry.getKey() == NodeComponentType.LIGHT) {
        entry.getValue().update();
      }
    }

    super.update();
  }

  @Override
  public void input() {
    components.values().forEach(NodeComponent::input);

    super.input();
  }

  @Override
  public void render() {
    if (components.containsKey(NodeComponentType.MAIN_RENDERINFO)) {
      components.get(NodeComponentType.MAIN_RENDERINFO).render();
    }

    super.render();
  }

  @Override
  public void renderWireframe() {

    if (components.containsKey(NodeComponentType.WIREFRAME_RENDERINFO)) {
      components.get(NodeComponentType.WIREFRAME_RENDERINFO).render();
    }

    super.renderWireframe();
  }

  @Override
  public void renderShadows() {
    if (components.containsKey(NodeComponentType.SHADOW_RENDERINFO)) {
      components.get(NodeComponentType.SHADOW_RENDERINFO).render();
    }

    super.renderShadows();
  }

  @Override
  public void record(RenderList renderList) {
    if (render) {
      if (!renderList.contains(getId())) {
        renderList.add(this);
        renderList.setChanged(true);
      }
    } else {
      if (renderList.contains(getId())) {
        renderList.remove(this);
        renderList.setChanged(true);
      }
    }

    super.record(renderList);
  }

  @Override
  public void shutdown() {

    components.values().forEach(NodeComponent::shutdown);

    super.shutdown();
  }

  @SuppressWarnings("unchecked")
  public <T> T getComponent(NodeComponentType type) {
    return (T) this.components.get(type);
  }
}
