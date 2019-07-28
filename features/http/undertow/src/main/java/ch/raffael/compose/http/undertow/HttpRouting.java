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

package ch.raffael.compose.http.undertow;

import ch.raffael.compose.ExtensionPoint;
import ch.raffael.compose.http.undertow.routing.RoutingDefinition;

import java.util.concurrent.atomic.AtomicReference;

import static io.vavr.API.*;

/**
 * Acceptor HTTP routing using the routing DSL.
 */
@ExtensionPoint.Acceptor
public interface HttpRouting<C> {

  HttpRouting route(RoutingDefinition<? super C> routingDef);

  class Default<C> {
    private final AtomicReference<RoutingDefinition<? super C>> routing = new AtomicReference<>(null);
    private final HttpRouting<C> api = new HttpRouting<>() {
      @Override
      public HttpRouting route(RoutingDefinition<? super C> routingDef) {
        if (!routing.compareAndSet(null, routingDef)) {
          throw new IllegalStateException("Routing definition already set");
        }
        return this;
      }
    };

    public HttpRouting<C> api() {
      return api;
    }

    public RoutingDefinition<? super C> definitions() {
      return Option(routing.get()).getOrElse(RoutingDefinition::empty);
    }
  }

}