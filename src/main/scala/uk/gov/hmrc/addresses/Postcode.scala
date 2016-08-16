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

import java.util.regex.Pattern

case class Postcode(area: String, district: String, sector: String, unit: String) {
  def outcode = area + district

  def incode = sector + unit

  def urlSafe = outcode + "+" + incode

  override lazy val toString = outcode + " " + incode
}


object Postcode {
  // The basic syntax of a postcode (ignores the rules on valid letter ranges because they don't matter here).
  private val oPattern = Pattern.compile("^[A-Z]{1,2}[0-9][0-9A-Z]?$")
  private val iPattern = Pattern.compile("^[0-9][A-Z]{2}$")

  /**
    * Performs normalisation and then checks the syntax, returning None if the string
    * cannot represent a well-formed postcode.
    */
  def cleanupPostcode(p: String): Option[Postcode] = {
    val norm = normalisePostcode(p)
    val space = norm.indexOf(' ')
    if (norm.length < 5) None

    else if (space < 0) {
      val incodeLength = norm.length - 3
      val out = norm.substring(0, incodeLength)
      val in = norm.substring(incodeLength, norm.length)
      checkSyntax(out, in)

    } else {
      val out = norm.substring(0, space)
      val in = norm.substring(space + 1)
      checkSyntax(out, in)
    }
  }

  private def checkSyntax(out: String, in: String): Option[Postcode] = {
    if (oPattern.matcher(out).matches() && iPattern.matcher(in).matches())
      Some(Postcode(out, in))
    else
      None
  }

  /** Removes excess whitespace from a postcode string. */
  def normalisePostcode(postcode: String): String = {
    postcode.trim.replaceAll("[ \\t]+", " ").toUpperCase
  }

  // p must be already cleaned up and normalised
  def apply(p: String): Postcode = {
    val space = p.indexOf(' ')
    require(space == p.length - 4, p)
    apply(p.substring(0, space), p.substring(space + 1))
  }

  def apply(outcode: String, incode: String): Postcode = {
    val (area, district) =
      if (Character.isDigit(outcode(1))) (outcode.substring(0, 1), outcode.substring(1))
      else (outcode.substring(0, 2), outcode.substring(2))
    val sector = incode.substring(0, 1)
    val unit = incode.substring(1)
    new Postcode(area, district, sector, unit)
  }
}
