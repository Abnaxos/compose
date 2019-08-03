/*
 *  Copyright (c) 2019 Raffael Herzog
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 */

package ch.raffael.compose.core;

import ch.raffael.compose.core.shutdown.ExecutorShutdownController;
import ch.raffael.compose.core.shutdown.ShutdownFeature;
import ch.raffael.compose.logging.Logging;
import io.vavr.collection.Seq;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

import static ch.raffael.compose.logging.Logging.logger;
import static io.vavr.API.*;

public final class ShutdownHooks {

  private static final Logger LOG = logger();

  private static final Object LOCK = new Object();
  @Nullable
  private static volatile ShutdownHooks INSTANCE = null;

  private final Thread shutdownHook = new Thread(ShutdownHooks.class.getName()) {
    @Override
    public void run() {
      var hooks = ShutdownHooks.this.hooks.get();
      hooks.forEach(Runnable::run);
      if (shutdownLogging) {
        shutdownLogging();
      }
    }
  };

  private final AtomicReference<Seq<Runnable>> hooks = new AtomicReference<>(Seq());
  private volatile boolean shutdownLogging = false;

  private ShutdownHooks() {
  }

  public static ShutdownHooks shutdownHooks() {
    var i = INSTANCE;
    if (i == null) {
      synchronized (LOCK) {
        i = INSTANCE;
        if (i == null) {
          i = new ShutdownHooks();
          Runtime.getRuntime().addShutdownHook(i.shutdownHook);
          INSTANCE = i;
        }
      }
    }
    return i;
  }

  public ShutdownHooks add(Runnable... runnable) {
    hooks.updateAndGet(r -> r.appendAll(Array(runnable)));
    return this;
  }

  public ShutdownHooks add(ShutdownFeature shutdown) {
    return add(() -> {
      var s = shutdown.shutdownController();
      if (s instanceof ExecutorShutdownController) {
        ((ExecutorShutdownController) s).performShutdown();
      } else {
        LOG.error("Cannot perform shutdown with {}", s);
      }
    });
  }

  public ShutdownHooks logging() {
    shutdownLogging = true;
    return this;
  }

  private void shutdownLogging() {
    Logging.shutdown();
  }

}