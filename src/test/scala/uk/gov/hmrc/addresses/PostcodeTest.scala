/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.addresses

import org.scalatest.FunSuite

//@RunWith(classOf[JUnitRunner])
class PostcodeTest extends FunSuite {

  test(
    """edge cases""") {
    assert(Postcode.normalisePostcode("") === "", "blank")
  }

  test(
    """Given a postcode in lowercase with excess whitespace (spaces and tabs),
       in which there is some whitespace between the incode and outcode,
       when normalisePostcode is used,
       then the postcode should become uppercase
       and surrounding whitespace should have been removed
       and there should be only one space between the outcode and incode
    """) {
    assert(Postcode.normalisePostcode(" sy21     9et ") === "SY21 9ET", "postcode with spaces")
    assert(Postcode.normalisePostcode("\tsy21\t9et\t") === "SY21 9ET", "postcode with tabs")
  }

  test(
    """Given a postcode in arbitrary format (as occasionally entered by a user)
       when cleanupPostcode is used,
       then the postcode should become uppercase
       and surrounding whitespace should have been removed
       and there should be only one space (which is inserted if neccessary) between the outcode and incode
    """) {
    assert(Postcode.cleanupPostcode("cr3 6qj") === Some(Postcode("CR", "3", "6", "QJ")), "lowercase postcode to uppercase")
    assert(Postcode.cleanupPostcode("CR36QJ") === Some(Postcode("CR", "3", "6", "QJ")), "missing space")
    assert(Postcode.cleanupPostcode("BT296QJ") === Some(Postcode("BT", "29", "6", "QJ")), "missing space")
    assert(Postcode.cleanupPostcode("B26QJ") === Some(Postcode("B", "2", "6", "QJ")), "missing space")
    assert(Postcode.cleanupPostcode("  CR3  6QJ  ") === Some(Postcode("CR", "3", "6", "QJ")), "normalise spaces")
    assert(Postcode.cleanupPostcode("  SW1a  1aa  ") === Some(Postcode("SW", "1A", "1", "AA")), "normalise spaces")
    assert(Postcode.cleanupPostcode("6QJ") === None, "short postcode")
    assert(Postcode.cleanupPostcode("12345678") === None, "not a postcode")
    assert(Postcode.cleanupPostcode("CR3QJJ") === None, "not a postcode")
    assert(Postcode.cleanupPostcode("CR3 QJJ") === None, "not a postcode")
    assert(Postcode.cleanupPostcode("CRV6QJ") === None, "not a postcode")
    assert(Postcode.cleanupPostcode("CRV 6QJ") === None, "not a postcode")
  }

  test("Split a postcode: case 1") {
    assert(Postcode("B9 1AZ") === Postcode("B", "9", "1", "AZ"))
  }

  test("Split a postcode: case 2") {
    assert(Postcode("B79 1AZ") === Postcode("B", "79", "1", "AZ"))
  }

  test("Split a postcode: case 3") {
    assert(Postcode("BT29 1AZ") === Postcode("BT", "29", "1", "AZ"))
  }

  test("Split a postcode: case 4") {
    assert(Postcode("SK9 1AZ") === Postcode("SK", "9", "1", "AZ"))
  }

  test("Split a postcode: case 5") {
    assert(Postcode("SW1A 1AA") === Postcode("SW", "1A", "1", "AA"))
  }

  test("toString") {
    assert(Postcode("SK9 1AZ").toString === "SK9 1AZ")
  }

  test("urlSafe") {
    assert(Postcode("SK9 1AZ").urlSafe === "SK9+1AZ")
  }
}
