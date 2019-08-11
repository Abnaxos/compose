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

package ch.raffael.compose.processor.test

import ch.raffael.compose.model.messages.Message
import spock.lang.Specification

import static ch.raffael.compose.processor.test.tools.ProcessorTestCase.compile

class ProvisionsSpec extends Specification {

  def "Directly implemented provisions work fine including shared/non-shared"() {
    when:
    def c = compile('c/provisions/basic/fine/implemented')

    then:
    c.allFine
    and:
    def s = c.shell()
    s.a() != null
    s.a() != s.a()
    and:
    s.b() != null
    s.b() == s.b()
  }

  def "Mounted implemented provisions work fine including shared/non-shared"() {
    when:
    def c = compile('c/provisions/basic/fine/mounted')

    then:
    c.allFine
    and:
    def s = c.shell()
    s.a() != null
    s.a() == s.a()
    and:
    s.b() != null
    s.b() != s.b()
  }

  def "A non-shared provision overriding a shared one without override=true is a compiler error"() {
    when:
    def c = compile('c/provisions/basic/problematic/unsharedOverridesShared')

    then:
    with (c.message()) {
      pos == c.marker('problematic-override')
      id == Message.Id.ProvisionOverrideMissing
    }
  }
}