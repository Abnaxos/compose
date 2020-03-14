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

package ch.raffael.meldioc.library.codec;

import ch.raffael.meldioc.ExtensionPoint;
import ch.raffael.meldioc.Feature;
import ch.raffael.meldioc.Provision;
import ch.raffael.meldioc.util.IOStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

import java.nio.charset.Charset;
import java.util.function.Consumer;

import static io.vavr.API.*;

@Feature
public interface GsonObjectCodecFeature extends ObjectCodecFeature {

  @Provision
  Gson defaultGson();

  @Feature
  abstract class Default implements GsonObjectCodecFeature {

    private final Configuration configuration = new Configuration();

    @Provision(shared = true)
    public Gson defaultGson() {
      var builder = new GsonBuilder();
      configuration.configurators.forEach(c -> c.accept(builder));
      return builder.create();
    }

    @Provision(shared = true)
    @Override
    public GsonObjectCodec.Factory objectCodecFactory() {
      return new GsonObjectCodec.Factory(
          defaultGson(), configuration.bufferSize.getOrElse(IOStreams.DEFAULT_BUFFER_SIZE), configuration.defaultCharset);
    }

    @ExtensionPoint
    protected Configuration gsonObjectCodecFeatureConfiguration() {
      return configuration;
    }
  }

  @ExtensionPoint.Acceptor
  final class Configuration {
    private static final Consumer<? super GsonBuilder> STANDARD = b -> {
      GsonObjectCodec.loadServiceLoaderTypeAdapters(b);
      GsonObjectCodec.registerVavr(b);
    };

    private Seq<Consumer<? super GsonBuilder>> configurators = Seq(STANDARD);
    private Option<Integer> bufferSize = None();
    private Option<Charset> defaultCharset = None();

    public Configuration removeStandardConfigurators() {
      configurators = configurators.remove(STANDARD);
      return this;
    }

    public Configuration configure(Consumer<? super GsonBuilder> configurator) {
      configurators = configurators.append(configurator);
      return this;
    }

    public Configuration bufferSize(int bufferSize) {
      if (this.bufferSize.isDefined()) {
        throw new IllegalStateException("Buffer size already set");
      }
      this.bufferSize = Some(bufferSize);
      return this;
    }

    public Configuration defaultCharset(Charset defaultCharset) {
      if (this.defaultCharset.isDefined()) {
        throw new IllegalStateException("Default charset already set");
      }
      this.defaultCharset = Some(defaultCharset);
      return this;
    }
  }
}