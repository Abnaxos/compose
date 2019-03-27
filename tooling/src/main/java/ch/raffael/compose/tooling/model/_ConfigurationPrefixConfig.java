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

import java.util.Optional;

import static ch.raffael.compose.util.fun.Fun.none;
import static ch.raffael.compose.util.fun.Fun.some;

/**
 * @since 2019-03-25
 */
@Immutable.Public
abstract class _ConfigurationPrefixConfig<S> extends ModelElementConfig<S> {

  private static final ModelAnnotationType TYPE = ModelAnnotationType.of(Configuration.Prefix.class);

  public static _ConfigurationPrefixConfig<Configuration.Prefix> of(Configuration.Prefix annotation) {
    return ConfigurationPrefixConfig.<Configuration.Prefix>builder()
        .source(annotation)
        .value(annotation.value())
        .build();
  }
  public abstract String value();

  @Override
  public final ModelAnnotationType type() {
    return TYPE;
  }
}
