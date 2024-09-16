package org.oreon.core.model;

import lombok.Getter;
import lombok.Setter;
import org.oreon.core.scenegraph.NodeComponent;

@Setter
@Getter
public class Model extends NodeComponent {

  private Mesh mesh;
  private Material material;

}
