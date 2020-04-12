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

package c.exceptions.extensionPoint;

import ch.raffael.meldioc.ExtensionPoint;
import ch.raffael.meldioc.Feature;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

@Feature
public class ExtensionPointProvisionThrowing {

  private final Configuration myConfiguration = new Configuration();

  @SuppressWarnings("RedundantThrows")
  @ExtensionPoint
  protected Configuration myConfiguration() throws Exception {
    return myConfiguration;
  }

  @ExtensionPoint.Acceptor
  public static final class Configuration {
    private Seq<Integer> ints = List.of();

    public void add(int myInt) {
      ints = ints.append(myInt);
    }
  }
}
