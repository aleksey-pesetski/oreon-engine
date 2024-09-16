package org.oreon.core.instanced;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;

@Data
public class InstancedHandler {

  private static InstancedHandler instance;

  private Lock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();

  public static synchronized InstancedHandler getInstance() {
    if (instance == null) {
      instance = new InstancedHandler();
    }
    return instance;
  }

  public void signalAll() {
    lock.lock();
    try {
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }
}
