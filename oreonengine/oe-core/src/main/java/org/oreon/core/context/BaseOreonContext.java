package org.oreon.core.context;

import lombok.Getter;
import lombok.Setter;
import org.oreon.core.BaseOreonRenderEngine;
import org.oreon.core.CoreEngine;
import org.oreon.core.platform.Input;
import org.oreon.core.platform.Window;
import org.oreon.core.scenegraph.BaseOreonCamera;

@Getter
public class BaseOreonContext<I extends Input, C extends BaseOreonCamera, W extends Window, R extends OreonResource>
    implements OreonContext<I, C, W> {

  private Config config;
  private I input;
  @Setter
  private BaseOreonRenderEngine renderEngine;
  @Setter
  private CoreEngine coreEngine;

  private C camera;
  private W window;
  private R resources;

  protected BaseOreonContext(final I input, final C camera, final W window, final R resources,
      final Config config, final BaseOreonRenderEngine renderEngine, final CoreEngine coreEngine) {
    this.input = input;
    this.camera = camera;
    this.window = window;
    this.resources = resources;
    this.config = config;
    this.renderEngine = renderEngine;
    this.coreEngine = coreEngine;
  }
}
