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

package ch.raffael.compose.tooling.model;

import ch.raffael.compose.Configuration;
import ch.raffael.compose.util.immutables.Immutable;
import org.immutables.value.Value;

import java.util.Optional;

import static ch.raffael.compose.util.fun.Fun.none;
import static ch.raffael.compose.util.fun.Fun.some;

/**
 * @since 2019-03-25
 */
@Immutable.Public
abstract class _ConfigurationConfig<S> extends ModelElementConfig<S> {

  private static final ModelAnnotationType TYPE = ModelAnnotationType.of(Configuration.class);

  public static ConfigurationConfig<Configuration> of(Configuration annotation) {
    return ConfigurationConfig.<Configuration>builder()
        .source(annotation)
        .key(annotation.key().isEmpty() ? none() : some(annotation.key()))
        .absolute(annotation.absolute())
        .build();
  }
  public abstract Optional<String> key();

  public abstract boolean absolute();

  @Override
  public final ModelAnnotationType type() {
    return TYPE;
  }
}
