package org.oreon.core.context;

import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.oreon.core.platform.Input;
import org.oreon.core.platform.Window;
import org.oreon.core.scenegraph.BaseOreonCamera;

@Log4j2
public abstract class ContextHolder<I extends Input, C extends BaseOreonCamera, W extends Window, OC extends OreonContext<I, C, W>> {

  private static final ThreadLocal<OreonContext<?, ?, ?>> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

  private ContextHolder() {
    throw new IllegalStateException();
  }

  public static OreonContext<?, ?, ?> getContext() {
    return Optional.ofNullable(CONTEXT_THREAD_LOCAL.get())
        .orElseThrow(() -> {
          log.info("ContextHolder : {}", CONTEXT_THREAD_LOCAL.toString());
          throw new IllegalStateException("No OreonContext available");
        });
  }

  public static void setContext(final OreonContext<?, ?, ?> context) {
    CONTEXT_THREAD_LOCAL.set(context);
  }

  public static void clearContext() {
    CONTEXT_THREAD_LOCAL.remove();
  }
}
