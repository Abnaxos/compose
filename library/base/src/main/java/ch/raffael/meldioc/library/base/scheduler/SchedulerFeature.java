/*
 *  Copyright (c) 2020 Raffael Herzog
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

package ch.raffael.meldioc.library.base.scheduler;

import ch.raffael.meldioc.Feature;
import ch.raffael.meldioc.Parameter;
import ch.raffael.meldioc.Provision;
import ch.raffael.meldioc.library.base.lifecycle.ShutdownFeature;
import ch.raffael.meldioc.library.base.threading.ThreadingFeature;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.Executor;

@Feature
public interface SchedulerFeature {

  @Provision
  Scheduler scheduler();

  @Feature
  @Parameter.Prefix("scheduler")
  abstract class Default implements SchedulerFeature {

    @Provision(singleton = true)
    @Override
    public Scheduler scheduler() {
      return DefaultScheduler.withExecutor(executor())
          .clock(clock())
          .readjustDuration(readjustDuration())
          .earlyRunTolerance(earlyRunTolerance())
          .build();
    }

    @Parameter
    protected Duration earlyRunTolerance() {
      return DefaultScheduler.DEFAULT_EARLY_RUN_TOLERANCE;
    }

    @Parameter
    protected Duration lateRunTolerance() {
      return DefaultScheduler.DEFAULT_LATE_RUN_TOLERANCE;
    }

    @Parameter
    protected Duration readjustDuration() {
      return DefaultScheduler.DEFAULT_READJUST_DURATION;
    }

    protected abstract Executor executor();

    protected Clock clock() {
      return Clock.systemDefaultZone();
    }
  }

  @Feature
  abstract class WithShutdown extends Default implements ShutdownFeature {
    @Provision(singleton = true)
    @Override
    public Scheduler scheduler() {
      var scheduler = (DefaultScheduler) super.scheduler();
      shutdownController().onPrepare(scheduler::shutdown);
      return scheduler;
    }
  }

  @Feature
  abstract class WithThreading extends Default implements ThreadingFeature {
    @Override
    protected Executor executor() {
      return workExecutor();
    }
  }

  @Feature
  abstract class WithThreadingAndShutdown extends WithShutdown implements ThreadingFeature {
    @Override
    protected Executor executor() {
      return workExecutor();
    }
  }
}
