package org.oreon.core.context;

import org.oreon.core.CoreEngine;
import org.oreon.core.BaseOreonRenderEngine;
import org.oreon.core.platform.Input;
import org.oreon.core.platform.Window;
import org.oreon.core.scenegraph.BaseOreonCamera;

public interface OreonContext<I extends Input, C extends BaseOreonCamera, W extends Window> {

  Config getConfig();

  I getInput();

  BaseOreonRenderEngine getRenderEngine();

  C getCamera();

  W getWindow();

  CoreEngine getCoreEngine();

  void setRenderEngine(BaseOreonRenderEngine renderEngine);

}
