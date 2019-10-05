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
import ch.raffael.compose.Feature.Mount;
import ch.raffael.compose.Parameter;
import ch.raffael.compose.Provision;
import ch.raffael.compose.Setup;
import ch.raffael.compose.codec.GsonObjectCodecFeature;
import ch.raffael.compose.core.lifecycle.LifecycleFeature;
import ch.raffael.compose.core.lifecycle.StartupActions;
import ch.raffael.compose.http.undertow.StandardHttpServerParams;
import ch.raffael.compose.http.undertow.UndertowBlueprint;
import ch.raffael.compose.http.undertow.UndertowServerFeature;
import ch.raffael.compose.http.undertow.handler.RequestLoggingHandler;
import ch.raffael.compose.http.undertow.routing.RoutingDefinition;
import ch.raffael.compose.logging.Logging;
import ch.raffael.compose.usecases.undertow.hello.security.HelloIdentityManager;
import ch.raffael.compose.usecases.undertow.hello.security.HelloRole;
import com.typesafe.config.Config;
import org.slf4j.Logger;

@Configuration
abstract class DefaultHelloAppContext implements HelloAppContext {

  private static final Logger LOG = Logging.logger();

  @Mount
  abstract LifecycleFeature.WithThreading lifecycleFeature();

  @Mount
  abstract GsonObjectCodecFeature.Default gsonObjectCodecFeature();

  @Mount
  abstract UndertowServerFeature.WithSharedWorkersAndShutdown<HelloRequestContext> undertowServerFeature();


  @Setup
  void startup(StartupActions startupActions) {
    startupActions.add(() -> undertowServerFeature().start());
    //startupActions.add(() -> {
    //  throw new Exception("Fail");
    //});
  }

  @Parameter
  String greeting() {
    return "Hello";
  }

  @Provision
  HelloRequests helloRequests() {
    return new HelloRequests(greeting());
  }

  @SuppressWarnings("CodeBlock2Expr")
  private RoutingDefinition<HelloRequestContext> routing() {
    return new RoutingDefinition<>() {{
      objectCodec(objectCodecFactory());
      path("/hello").route(() -> {
        get().producePlainText()
            .apply(query("name").asString(), helloRequests()::text);
        path().captureString().route(name ->
            get().producePlainText()
                .apply(name, helloRequests()::text));
      });
      path("/rest/hello").route(() -> {
        post().accept(RestHelloRequest.class).produce(RestHelloResponse.class)
            .apply(helloRequests()::json);
      });
    }};
  }

  private RoutingDefinition<HelloRequestContext> mergedRouting() {
    var paramHello = new RoutingDefinition<HelloRequestContext>() {{
      get().producePlainText().nonBlocking()
          .apply(query("name").asString(), helloRequests()::text);
    }};
    var pathHello = new RoutingDefinition<HelloRequestContext>() {{
      path().captureString().route(name ->
          get().producePlainText()
              .apply(name, helloRequests()::text));
    }};
    var restHello = new RoutingDefinition<HelloRequestContext>() {{
        post().accept(RestHelloRequest.class).produce(RestHelloResponse.class)
            .apply(helloRequests()::json);
    }};
//    return new RoutingDefinition<>() {{
//      objectCodec(new GsonCodecFactory());
//      path("hello").merge(paramHello);
//      path("hello").merge(pathHello);
//      path("rest/hello").merge(restHello);
//    }};
    return new RoutingDefinition<>() {{
      objectCodec(objectCodecFactory());
      path("hello").route(() -> {
        restrict(HelloRole.class, HelloRole.USER);
        merge(paramHello);
        merge(pathHello);
      });
      path("rest/hello").route(() -> {
        restrict(HelloRole.class, HelloRole.ADMIN);
        merge(restHello);
      });
    }};
  }

  @Setup
  void setupUndertow(UndertowBlueprint<HelloRequestContext> config) {
    config.requestContextFactory(
        __ -> DefaultHelloRequestContextShell.builder().mountParent(this).config(allConfig()).build())
        .handler(n -> RequestLoggingHandler.info(LOG, n))
        .basicSecurity(new HelloIdentityManager())
        .mainHandler(mergedRouting())
        .http(httpServerAddress(), httpServerPort());
  }

  @Parameter(Parameter.ALL)
  abstract Config allConfig();

  @Parameter(StandardHttpServerParams.PORT)
  abstract int httpServerPort();

  @Parameter(StandardHttpServerParams.ADDRESS)
  String httpServerAddress() {
    return StandardHttpServerParams.ADR_ALL;
  }
}
