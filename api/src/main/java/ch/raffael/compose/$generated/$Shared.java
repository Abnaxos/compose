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

package ch.raffael.compose.$generated;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class $Shared<T> {

  @Nullable
  private volatile Provider<T> provider;

  @Nullable
  private T value;

  private $Shared(@Nonnull Provider<T> provider) {
    this.provider = provider;
  }

  public static <T> $Shared<T> of(@Nonnull Provider<T> provider) {
    return new $Shared<>(provider);
  }

  @Nonnull
  public T get() throws Throwable {
    if (provider != null) {
      synchronized (this) {
        if (provider != null) {
          //noinspection ConstantConditions
          value = Objects.requireNonNull(provider.get(), "provider.get()");
          // JMM: this write will flush: `provider=null` happens-before `if(provider!=null)`
          provider = null;
        }
      }
    }
    //noinspection ConstantConditions
    return value;
  }

  @FunctionalInterface
  public interface Provider<T> {
    @Nonnull
    T get() throws Throwable;
  }

}