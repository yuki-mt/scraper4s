package com.yukimt.scrape.element

import org.specs2.mutable.Specification
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class HtmlElementSpec extends Specification{
  sequential

  "HtmlElement" should {
    "get ajacents, parant, chilren" in {
      implicit val driver = new HtmlUnitDriver
      driver.get("http://localhost:3000/view")
      1 === 1
    }
  }
}
