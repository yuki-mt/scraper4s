package com.yukimt.scrape.element

import org.specs2.mutable.Specification
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class ATagElementSpec extends Specification{
  sequential
  implicit val driver = new HtmlUnitDriver(true)
  driver.get("http://localhost:3000/view")
  val parser = new Parser(driver)
  val element = parser.findElement("a").get.asATagElement.get

  "ATagElement" should {
    "set query string" in {
      element.setQueryString(Map("a"->"b", "c"->"d"))
      element.url === Some("http://localhost:3000/?a=b&c=d")
    }

    "set uri" in {
      element.setUri("/abc")
      element.url === Some("http://localhost:3000/abc")
    }

    "open in new window" in {
      element.setUri("/")
      driver.getTitle === "Express Sample Title"
      val id = driver.getWindowHandle
      driver.switchTo.window(element.openInNewWindow.id)
      driver.getTitle === ""
    }
  }
}
