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

package ch.raffael.meldioc.library.codec;

import ch.raffael.meldioc.ExtensionPoint;
import ch.raffael.meldioc.Feature;
import ch.raffael.meldioc.Provision;
import ch.raffael.meldioc.util.IOStreams;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.gson.VavrGson;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Consumer;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;


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
    public enum Standard implements Consumer<GsonBuilder> {
      SERVICE_LOADER {
        @Override
        public void accept(GsonBuilder b) {
          GsonObjectCodec.loadServiceLoaderTypeAdapters(b);
        }
      },
      JAVA_TIME {
        @Override
        public void accept(GsonBuilder b) {
          Converters.registerAll(b);
        }
      },
      VAVR {
        @Override
        public void accept(GsonBuilder b) {
          VavrGson.registerAll(b);
        }
      },
    }

    private Seq<Consumer<? super GsonBuilder>> configurators = List.of(Standard.values());
    private Option<Integer> bufferSize = none();
    private Option<Charset> defaultCharset = none();

    public final Configuration removeStandardConfigurators(Standard... remove) {
      if (remove == null || remove.length == 0) {
        remove = Standard.values();
      }
      configurators = configurators.removeAll(Arrays.asList(remove));
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
      this.bufferSize = some(bufferSize);
      return this;
    }

    public Configuration defaultCharset(Charset defaultCharset) {
      if (this.defaultCharset.isDefined()) {
        throw new IllegalStateException("Default charset already set");
      }
      this.defaultCharset = some(defaultCharset);
      return this;
    }
  }
}
