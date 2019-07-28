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

package ch.raffael.compose.http.undertow.routing;

import ch.raffael.compose.http.undertow.codec.Decoder;
import ch.raffael.compose.http.undertow.codec.Encoder;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * TODO JavaDoc
 */
public final class ActionHandler<C, B, R> implements HttpHandler {

  private final Decoder<? super C, ? extends B> decoder;
  private final Encoder<? super C, ? super R> encoder;
  private final Invoker<? super C, ? super B, ? extends R> invoker;

  ActionHandler(Decoder<? super C, ? extends B> decoder, Encoder<? super C, ? super R> encoder, Invoker<? super C, ? super B, ? extends R> invoker) {
    this.decoder = decoder;
    this.encoder = encoder;
    this.invoker = invoker;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    C context = context();
    B body = decoder.decode(exchange, context);
    R response = invoker.invoke(exchange, context, body);
    encoder.encode(exchange, context, response);
  }

  private C context() {
    return null;
  }

  interface Invoker<C, B, R> {
    R invoke(HttpServerExchange exchange, C context, B body) throws Exception;
  }

  @FunctionalInterface
  public interface Action0<C, R> {
    R perform(C ctx) throws Exception;
  }

  @FunctionalInterface
  public interface Action1<C, P1, R> {
    R perform(C ctx, P1 arg1);
  }

  @FunctionalInterface
  public interface Action2<C, P1, P2, R> {
    R perform(C ctx, P1 arg1, P2 arg2);
  }

  @FunctionalInterface
  public interface Action3<C, P1, P2, P3, R> {
    R perform(C ctx, P1 arg1, P2 arg2, P3 arg3);
  }
}

