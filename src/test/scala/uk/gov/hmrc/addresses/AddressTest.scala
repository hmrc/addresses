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
import uk.gov.hmrc.addresses.Countries.UK

class AddressTest extends FunSuite {

  test("nonEmptyFields") {
    assert(Address(Nil, None, None, "FX1 1ZZ", None, UK).nonEmptyFields === List("FX1 1ZZ"))

    assert(Address(List("1", "2"), None, None, "FX1 1ZZ", None, UK).nonEmptyFields === List("1", "2", "FX1 1ZZ"))

    assert(Address(Nil, Some("town"), None, "FX1 1ZZ", None, UK).nonEmptyFields === List("town", "FX1 1ZZ"))

    assert(Address(Nil, None, Some("county"), "FX1 1ZZ", None, UK).nonEmptyFields === List("county", "FX1 1ZZ"))

    assert(Address(List("1", "2"), Some("town"), Some("county"), "FX1 1ZZ", Some("XX-YYY"), UK).nonEmptyFields ===
      List("1", "2", "town", "county", "FX1 1ZZ"))
  }

  test("truncatedAddress") {
    assert(
      Address(List("1", "2"), Some("town"), Some("county"), "FX1 1ZZ", Some("XX-YYY"), UK).truncatedAddress(30) ===
        Address(List("1", "2"), Some("town"), Some("county"), "FX1 1ZZ", Some("XX-YYY"), UK))

    assert(
      Address(List("a23456789 123456789", "b23456789 123456789"), Some("c23456789 123456789"), Some("d123456789 123456789"),
        "FX1 1ZZ", Some("XX-YYY"), UK).truncatedAddress(12) ===
        Address(List("a23456789 12", "b23456789 12"), Some("c23456789 12"), Some("d123456789"), "FX1 1ZZ", Some("XX-YYY"), UK))
  }
}
