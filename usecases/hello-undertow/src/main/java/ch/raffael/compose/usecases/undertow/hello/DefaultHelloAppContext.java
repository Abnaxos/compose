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

package ch.raffael.compose.usecases.undertow.hello;

import ch.raffael.compose.Configuration;
import ch.raffael.compose.Feature;
import ch.raffael.compose.Feature.DependsOn;
import ch.raffael.compose.Feature.Mount;
import ch.raffael.compose.Parameter;
import ch.raffael.compose.Setup;
import ch.raffael.compose.core.shutdown.ShutdownFeature;
import ch.raffael.compose.core.threading.JavaThreadPoolFeature;
import ch.raffael.compose.core.threading.ThreadingFeature;
import ch.raffael.compose.http.undertow.DefaultUndertowServerFeature;
import ch.raffael.compose.http.undertow.HttpRouting;
import ch.raffael.compose.http.undertow.UndertowBuilderConfiguration;
import ch.raffael.compose.http.undertow.handler.RequestLoggingHandler;
import ch.raffael.compose.http.undertow.routing.RoutingDefinition;
import ch.raffael.compose.logging.Logging;
import com.typesafe.config.Config;
import org.slf4j.Logger;

/**
 * TODO javadoc
 */
@Configuration
abstract class DefaultHelloAppContext implements HelloAppContext {

  private static final Logger LOG = Logging.logger();

  private final HttpRouting.Default<HelloRequestContext> httpRouterEp = new HttpRouting.Default<>();

  @Mount
  abstract ShutdownFeature.WithThreadingWorker shutdownFeature();

  @Mount
  abstract JavaThreadPoolFeature.WithShutdown threadingFeature();

  @Mount
  abstract ThreadingFeature.WithSystemForkJoinPool systemForkJoinFeature();

  @Mount
  abstract MyUndertowServerFeature undertowServerFeature();

  @Parameter
  String greeting() {
    return "Hello";
  }

  void start() {
    undertowServerFeature().start();
  }

  void shutdown() {
    shutdownFeature().shutdownController().performShutdown().await();
  }

  @Setup
  void routing(HttpRouting<? extends HelloRequestContext> router) {
    router.route(new RoutingDefinition<HelloRequestContext>() {{
      path("/hello").route(() -> {
        get().producePlainText()
            .with(query("name").asString().orElse("whoever you are"), (ctx, n) -> greeting() + " " + n);
        path().captureString().route(valName ->
            get().producePlainText()
                .with(valName, (ctx, name) -> greeting() + " " + name));
      });
    }});
  }

  @Setup
  void setupUndertow(UndertowBuilderConfiguration<? super DefaultHelloRequestContext> config) {
    config.requestContextFactory(
        __ -> DefaultHelloRequestContextShell.builder().mountParent(this).config(allConfig()).build())
        .handler(n -> RequestLoggingHandler.info(LOG, n))
        .compressHandler()
        .http(httpServerAddress(), httpServerPort());
  }

  @Parameter(Parameter.ALL)
  abstract Config allConfig();

  @Parameter("http-server.port")
  abstract int httpServerPort();

  @Parameter("http-server.address")
  String httpServerAddress() {
    return "0.0.0.0";
  }

  @Feature
  static abstract class MyUndertowServerFeature extends DefaultUndertowServerFeature.WithShutdown<HelloRequestContext>
      implements @DependsOn ShutdownFeature {
    void start() {
      undertow();
    }
  }

}
