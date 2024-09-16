package org.oreon.core.instanced;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.oreon.core.context.ContextHolder;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.RenderList;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.IntegerReference;

@Getter
@Setter
public abstract class InstancedObject extends Renderable {

  private int instanceCount;
  private Vec3f[] positions;

  private List<Matrix4f> worldMatrices = new ArrayList<>();
  private List<Matrix4f> modelMatrices = new ArrayList<>();

  private List<Integer> highPolyIndices = new ArrayList<>();
  private List<Integer> lowPolyIndices = new ArrayList<>();

  private List<Renderable> lowPolyObjects = new ArrayList<>();
  private List<Renderable> highPolyObjects = new ArrayList<>();

  private IntegerReference highPolyInstanceCount;
  private IntegerReference lowPolyInstanceCount;
  private int highPolyRange;

  public void update() {

    getHighPolyIndices().clear();

    int index = 0;

    for (Matrix4f transform : getWorldMatrices()) {
      if (transform.getTranslation().sub(ContextHolder.getContext().getCamera().getPosition()).length()
          < highPolyRange) {
        getHighPolyIndices().add(index);
      }

      index++;
    }
    getHighPolyInstanceCount().setValue(getHighPolyIndices().size());
  }

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
  }

  public void renderLowPoly() {
    lowPolyObjects.forEach(Renderable::render);
  }

  public void renderHighPoly() {
    highPolyObjects.forEach(Renderable::render);
  }

  public void renderLowPolyShadows() {
    lowPolyObjects.forEach(Renderable::renderShadows);
  }

  public void renderHighPolyShadows() {
    highPolyObjects.forEach(Renderable::renderShadows);
  }
}