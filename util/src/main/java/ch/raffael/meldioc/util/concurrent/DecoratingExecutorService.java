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

package ch.raffael.meldioc.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.stream.Collectors.toList;

/**
 * TODO JavaDoc
 */
public abstract class DecoratingExecutorService<D extends ExecutorService> extends SecureExecutorServiceWrapper<D> {

  protected DecoratingExecutorService(D delegate) {
    super(delegate);
  }

  protected abstract Runnable decorateRunnable(Runnable original);
  protected abstract <T> Callable<T> decorateCallable(Callable<? extends T> original);

  @Override
  public void execute(Runnable command) {
    delegate.execute(decorateRunnable(command));
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return delegate.submit(decorateCallable(task));
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    return delegate.submit(decorateRunnable(task), result);
  }

  @Override
  public Future<?> submit(Runnable task) {
    return delegate.submit(decorateRunnable(task));
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return delegate.invokeAll(tasks.stream().map(this::decorateCallable).collect(toList()));
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return delegate.invokeAll(tasks.stream().map(this::decorateCallable).collect(toList()), timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return delegate.invokeAny(tasks.stream().map(this::decorateCallable).collect(toList()));
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return delegate.invokeAny(tasks.stream().map(this::decorateCallable).collect(toList()), timeout, unit);
  }
}
