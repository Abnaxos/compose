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

package ch.raffael.meldioc.library.base.jmx.registry;

import io.vavr.collection.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * TODO JavaDoc
 */
public
interface ObjectNameBuilder {

  String TYPE_PROPERTY = "type";
  String NAME_PROPERTY = "name";

  ObjectNameBuilder type(String type);

  ObjectNameBuilder type(Class<?> type, boolean verbatim);

  default ObjectNameBuilder type(Class<?> type) {
    return type(type, true);
  }

  ObjectNameBuilder name(String name);

  ObjectNameBuilder property(String name, String value);

  ObjectNameBuilder properties(Map<String, String> properties);

  ObjectNameBuilder properties(java.util.Map<String, String> properties);

  ObjectNameBuilder domain(String domain);

  ObjectName toObjectName() throws MalformedObjectNameException;
}
